package com.project.geoalert.service;

import com.project.geoalert.dto.AlertRequest;
import com.project.geoalert.entity.Alert;
import com.project.geoalert.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WeatherScheduler {

    @Autowired
    private AlertService alertService;

    @Autowired
    private AlertRepository alertRepository;

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Value("${openweathermap.api.url}")
    private String apiUrl;

    @Value("${openweathermap.lat}")
    private Double lat;

    @Value("${openweathermap.lon}")
    private Double lon;

    private final WebClient webClient = WebClient.create();

    @Scheduled(fixedRate = 60000)
    public void fetchWeatherAndCreateAlert() {
        String url = String.format("%s?lat=%s&lon=%s&appid=%s&units=metric",
                apiUrl, lat, lon, apiKey);

        try {
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            processWeatherResponse(response);

        } catch (Exception e) {
            System.out.println("Weather fetch failed: " + e.getMessage());
        }
    }

    private void processWeatherResponse(String response) {
        try {
            // Extract weather condition id
            int idIndex = response.indexOf("\"id\":");
            if (idIndex == -1) return;

            int start = idIndex + 5;
            int end = response.indexOf(",", start);
            int weatherId = Integer.parseInt(response.substring(start, end).trim());

            // Extract description
            int descIndex = response.indexOf("\"description\":\"");
            String description = "Weather Alert";
            if (descIndex != -1) {
                int dStart = descIndex + 15;
                int dEnd = response.indexOf("\"", dStart);
                description = response.substring(dStart, dEnd);
            }

            String type = mapWeatherIdToType(weatherId);
            String severity = mapWeatherIdToSeverity(weatherId);

            if (type == null) return; // normal weather, no alert needed

            if (alertRepository.existsTodayByType(type)) {
                System.out.println("Alert already exists today for type: " + type);
                return;
            }

            AlertRequest request = new AlertRequest();
            request.setTitle("Auto Alert: " + description);
            request.setType(type);
            request.setSeverity(severity);
            request.setLatitude(lat);
            request.setLongitude(lon);
            request.setRadiusKm(10.0);

            Alert alert = alertService.createAlert(request);
            System.out.println("Auto alert created: " + alert.getId() + " - " + alert.getTitle());

        } catch (Exception e) {
            System.out.println("Weather processing failed: " + e.getMessage());
        }
    }

    private String mapWeatherIdToType(int id) {
        if (id >= 200 && id < 210) return "THUNDERSTORM"; // violent thunderstorm only
        if (id == 502 || id == 503 || id == 504) return "FLOOD"; // heavy/very heavy/extreme rain only
        if (id == 511) return "FLOOD"; // freezing rain
        if (id >= 600 && id < 602) return "SNOWSTORM"; // heavy snow only
        if (id == 781) return "CYCLONE"; // tornado
        return null; // everything else — moderate rain, drizzle, fog, clouds — no alert
    }

    private String mapWeatherIdToSeverity(int id) {
        if (id == 781) return "EXTREME";
        if (id == 504 || id >= 200 && id < 210) return "HIGH";
        if (id == 502 || id == 503) return "MEDIUM";
        return "LOW";
    }
}