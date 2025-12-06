package com.traffic.notification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnomalyEvent {
    private String anomalyType;
    private String description;
    private String sourceIp;
    private int sizeBytes;
}