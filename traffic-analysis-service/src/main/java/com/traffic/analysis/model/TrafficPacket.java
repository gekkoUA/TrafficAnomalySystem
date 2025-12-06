package com.traffic.analysis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrafficPacket {
    private String sourceIp;
    private String destIp;
    private int sizeBytes;
    private int destPort;
}