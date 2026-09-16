package com.project.geoalert.service;

import com.project.geoalert.dto.AlertRequest;
import com.project.geoalert.entity.Alert;
import com.project.geoalert.entity.Notification;
import com.project.geoalert.entity.User;
import com.project.geoalert.repository.AlertRepository;
import com.project.geoalert.repository.NotificationRepository;
import com.project.geoalert.repository.UserRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private AIService aiService;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public Alert createAlert(AlertRequest request) {
        Point location = geometryFactory.createPoint(
                new Coordinate(request.getLongitude(), request.getLatitude())
        );

        String safetyInstructions = aiService.generateSafetyInstructions(
                request.getType(), request.getSeverity()
        );

        Alert alert = Alert.builder()
                .title(request.getTitle())
                .type(request.getType())
                .severity(request.getSeverity())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .radiusKm(request.getRadiusKm())
                .location(location)
                .safetyInstructions(safetyInstructions)
                .build();

        Alert savedAlert = alertRepository.save(alert);

        double radiusMeters = request.getRadiusKm() * 1000;

        List<User> affectedUsers = userRepository.findUsersWithinRadius(
                request.getLatitude(),
                request.getLongitude(),
                radiusMeters
        );

        for (User user : affectedUsers) {
            messagingTemplate.convertAndSend("/topic/alerts", savedAlert);
            Notification notification = Notification.builder()
                    .userId(user.getId())
                    .alertId(savedAlert.getId())
                    .build();
            notificationRepository.save(notification);
        }

        return savedAlert;
    }

    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }
}