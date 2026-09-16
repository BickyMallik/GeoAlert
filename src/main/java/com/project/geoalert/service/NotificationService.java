package com.project.geoalert.service;

import com.project.geoalert.entity.Notification;
import com.project.geoalert.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> getNotificationByUser(Long userId){
        return notificationRepository.findByUserId(userId);
    }
}
