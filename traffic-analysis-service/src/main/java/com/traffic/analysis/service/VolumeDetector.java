package com.traffic.analysis.service;

import com.traffic.analysis.model.AnomalyReport;
import org.springframework.stereotype.Component;
import com.traffic.analysis.model.TrafficPacket;

@Component
public class VolumeDetector implements AnomalyDetector {
    private static final int MAX_SIZE = 1500;

    @Override
    public AnomalyReport analyze(TrafficPacket packet) {
        if (packet.getSizeBytes() > MAX_SIZE) {
            return AnomalyReport.builder()
                    .anomalyType("High Volume")
                    .description("Packet size " + packet.getSizeBytes() + " exceeds limit")
                    .build();
        }
        return null;
    }
}