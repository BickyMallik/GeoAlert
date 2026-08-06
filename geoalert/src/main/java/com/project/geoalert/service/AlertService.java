package com.project.geoalert.service;

import com.project.geoalert.dto.AlertRequest;
import com.project.geoalert.entity.Alert;
import com.project.geoalert.entity.Notification;
import com.project.geoalert.entity.User;
import com.project.geoalert.repository.AlertRepository;
import com.project.geoalert.repository.NotificationRepository;
import com.project.geoalert.repository.UserRepository;
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
    private HaversineService haversineService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Alert createAlert(AlertRequest request){
        Alert alert =Alert.builder()
                .title(request.getTitle())
                .type(request.getType())
                .severity(request.getSeverity())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .radiusKm(request.getRadiusKm())
                .build();

        Alert savedAlert = alertRepository.save(alert);

        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers){
            double distance = haversineService.calculateDistance(
                    savedAlert.getLatitude(), savedAlert.getLongitude(),
                    user.getLatitude(), user.getLongitude()
            );

            if (distance <= savedAlert.getRadiusKm()){
                messagingTemplate.convertAndSend("/topic/alerts", savedAlert);
                Notification notification = Notification.builder()
                        .userId(user.getId())
                        .alertId(savedAlert.getId())
                        .build();
                notificationRepository.save(notification);
            }
        }

        return savedAlert;
    }

    public List<Alert> getAllAlerts(){
        return alertRepository.findAll();
    }
}
