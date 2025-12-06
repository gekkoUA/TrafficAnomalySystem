package com.traffic.analysis.service;

import com.traffic.analysis.model.AnomalyReport;
import com.traffic.analysis.model.TrafficPacket;

public interface AnomalyDetector {
    AnomalyReport analyze(TrafficPacket packet);
}