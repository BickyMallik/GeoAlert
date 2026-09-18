package com.project.geoalert.controller;

import com.project.geoalert.dto.AlertRequest;
import com.project.geoalert.entity.Alert;
import com.project.geoalert.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @PostMapping
    public Alert createAlert(@RequestBody AlertRequest request){
        return alertService.createAlert(request);
    }

    @GetMapping
    public List<Alert> getAllAlerts(){
        return alertService.getAllAlerts();
    }
}
