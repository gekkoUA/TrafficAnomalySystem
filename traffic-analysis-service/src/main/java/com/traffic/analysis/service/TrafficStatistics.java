package com.traffic.analysis.service;

import com.traffic.analysis.model.TrafficPacket;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TrafficStatistics {
    public List<TrafficPacket> filterAndSortStream(PacketRegistry registry, int minSize) {
        return registry.stream()
                .filter(p -> p.getSizeBytes() > minSize)
                .sorted(Comparator.comparingInt(TrafficPacket::getSizeBytes))
                .collect(Collectors.toList());
    }
}