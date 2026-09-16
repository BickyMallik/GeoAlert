package com.project.geoalert.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AIService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.api.url}")
    private String apiUrl;

    @Value("${openrouter.model}")
    private String model;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String generateSafetyInstructions(String alertType, String severity) {
        String prompt = String.format(
                "A %s severity %s disaster alert has been issued. " +
                        "Give 5 short, clear safety instructions for affected citizens. " +
                        "Be direct and practical. No introduction, just the instructions.",
                severity, alertType
        );

        String requestBody = String.format("""
                {
                    "model": "%s",
                    "messages": [
                        {
                            "role": "user",
                            "content": "%s"
                        }
                    ]
                }
                """, model, prompt);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            return parseContent(response.body());

        } catch (Exception e) {
            return "Stay calm. Follow local authority instructions. Move to safety.";
        }
    }

    private String parseContent(String responseBody) {
        try {
            int contentIndex = responseBody.indexOf("\"content\":\"");
            if (contentIndex == -1) return "Follow local authority instructions.";
            int start = contentIndex + 11;
            int end = responseBody.indexOf("\"", start);
            return responseBody.substring(start, end).replace("\\n", "\n");
        } catch (Exception e) {
            return "Follow local authority instructions.";
        }
    }
}