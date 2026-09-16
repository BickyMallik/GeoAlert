package com.project.geoalert.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertRequest {
    private String title;
    private String type;
    private String severity;
    private Double latitude;
    private Double longitude;
    private Double radiusKm;
}
