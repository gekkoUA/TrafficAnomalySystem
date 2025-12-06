package com.traffic.analysis.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnomalyEvent {
    private String anomalyType;
    private String description;
    private String sourceIp;
    private int sizeBytes;
}