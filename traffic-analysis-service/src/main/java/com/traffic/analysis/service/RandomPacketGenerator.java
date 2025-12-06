package com.traffic.analysis.service;

import com.traffic.analysis.model.TrafficPacket;
import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class RandomPacketGenerator implements PacketSource {
    private final Random random = new Random();

    @Override
    public TrafficPacket getNextPacket() {
        String srcIp = "192.168.0." + random.nextInt(255);
        int size = random.nextInt(2500);
        int port = random.nextBoolean() ? 80 : 22;

        return new TrafficPacket(srcIp, "10.0.0.1", size, port);
    }
}