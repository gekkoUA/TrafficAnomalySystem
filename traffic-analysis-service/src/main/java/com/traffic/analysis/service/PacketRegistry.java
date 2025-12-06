package com.traffic.analysis.service;

import com.traffic.analysis.model.TrafficPacket;
import java.util.ArrayList;
import java.util.Collection;

public class PacketRegistry extends ArrayList<TrafficPacket> {
    public PacketRegistry() { super(); }

    @Override
    public boolean add(TrafficPacket packet) {
        if (packet != null && packet.getSizeBytes() > 0) {
            return super.add(packet);
        }
        return false;
    }
}