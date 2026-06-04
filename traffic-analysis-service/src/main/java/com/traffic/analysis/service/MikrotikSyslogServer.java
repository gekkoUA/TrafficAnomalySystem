package com.traffic.analysis.service;

import com.traffic.analysis.model.TrafficPacket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MikrotikSyslogServer {

    private final TrafficService trafficService;

    private final Pattern ipPortPattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)->(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)");
    private final Pattern lenPattern = Pattern.compile("len (\\d+)");

    @PostConstruct
    public void startListening() {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(514)) {
                System.out.println(">>> [UDP SERVER] Listening and Parsing MikroTik logs on port 514...");
                byte[] buffer = new byte[2048];

                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());

                    if (message.contains("MTIK_TRAFFIC")) {
                        Matcher ipMatcher = ipPortPattern.matcher(message);
                        Matcher lenMatcher = lenPattern.matcher(message);

                        if (ipMatcher.find() && lenMatcher.find()) {
                            String srcIp = ipMatcher.group(1);
                            String destIp = ipMatcher.group(3);
                            int destPort = Integer.parseInt(ipMatcher.group(4));
                            int sizeBytes = Integer.parseInt(lenMatcher.group(1));

                            TrafficPacket trafficPacket = new TrafficPacket(srcIp, destIp, sizeBytes, destPort);

                            trafficService.processRealPacket(trafficPacket);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println(">>> [UDP SERVER] Error: " + e.getMessage());
            }
        }).start();
    }
}