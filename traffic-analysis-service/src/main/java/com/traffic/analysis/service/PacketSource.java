package com.traffic.analysis.service;

import com.traffic.analysis.model.TrafficPacket;

public interface PacketSource {
    TrafficPacket getNextPacket();
}