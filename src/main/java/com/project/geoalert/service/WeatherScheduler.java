package com.project.geoalert.service;

import com.project.geoalert.dto.AlertRequest;
import com.project.geoalert.entity.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WeatherScheduler {

    @Autowired
    private AlertService alertService;

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
        if (id >= 200 && id < 300) return "THUNDERSTORM";
        if (id >= 300 && id < 400) return "DRIZZLE";
        if (id >= 500 && id < 600) return "FLOOD";
        if (id >= 600 && id < 700) return "SNOWSTORM";
        if (id >= 700 && id < 800) return "FOG";
        return null; // clear or cloudy — no alert
    }

    private String mapWeatherIdToSeverity(int id) {
        if (id >= 200 && id < 210) return "HIGH";
        if (id >= 500 && id < 502) return "MEDIUM";
        if (id >= 502) return "HIGH";
        if (id >= 600 && id < 602) return "MEDIUM";
        return "LOW";
    }
}