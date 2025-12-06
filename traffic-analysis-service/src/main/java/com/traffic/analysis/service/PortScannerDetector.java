package com.traffic.analysis.service;

import com.traffic.analysis.model.AnomalyReport;
import org.springframework.stereotype.Component;
import com.traffic.analysis.model.TrafficPacket;

@Component
public class PortScannerDetector implements AnomalyDetector {
    @Override
    public AnomalyReport analyze(TrafficPacket packet) {
        if (packet.getDestPort() == 22) {
            return AnomalyReport.builder()
                    .anomalyType("Security Breach")
                    .description("Attempted access to closed port 22 (SSH)")
                    .build();
        }
        return null;
    }
}