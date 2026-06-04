package com.traffic.analysis.service;

import com.traffic.analysis.config.RabbitConfig;
import com.traffic.analysis.document.AnomalyDocument;
import com.traffic.analysis.model.AnomalyEvent;
import com.traffic.analysis.model.AnomalyReport;
import com.traffic.analysis.model.TrafficLog;
import com.traffic.analysis.model.TrafficPacket;
import com.traffic.analysis.repository.AnomalySearchRepository;
import com.traffic.analysis.repository.ReportRepository;
import com.traffic.analysis.repository.TrafficLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TrafficService {

    private final TrafficLogRepository trafficLogRepository;
    private final ReportRepository reportRepository;
    private final S3Service s3Service;
    private final RabbitTemplate rabbitTemplate;
    private final AnomalySearchRepository searchRepository;

    private final Map<String, Integer> packetCounts = new ConcurrentHashMap<>();
    private long lastResetTime = System.currentTimeMillis();
    private static final int DDOS_THRESHOLD = 10;

    public List<AnomalyReport> getAllReports() { return reportRepository.findAll(); }

    public List<AnomalyDocument> searchAnomalies(String ip) {
        return searchRepository.findBySourceIp(ip);
    }

    public String uploadEvidence(Integer reportId, MultipartFile file) {
        AnomalyReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        String imageUrl = s3Service.uploadFile(file);
        report.setImageUrl(imageUrl);
        reportRepository.save(report);
        return imageUrl;
    }

    public void processRealPacket(TrafficPacket packet) {
        long now = System.currentTimeMillis();

        if (now - lastResetTime > 1000) {
            packetCounts.clear();
            lastResetTime = now;
        }

        int count = packetCounts.getOrDefault(packet.getSourceIp(), 0) + 1;
        packetCounts.put(packet.getSourceIp(), count);

        if (count == DDOS_THRESHOLD) {
            triggerAlert(packet, "DDoS Attack", "High packet rate detected: " + count + " pkts/sec");
        }

        if (packet.getDestPort() == 22) {
            triggerAlert(packet, "Security Breach", "Attempted access to closed port 22 (SSH)");
        }

        if (packet.getSizeBytes() > 1000) {
            triggerAlert(packet, "Real High Volume", "Heavy packet detected! Size: " + packet.getSizeBytes() + " bytes");
        }
    }

    private void triggerAlert(TrafficPacket packet, String type, String description) {
        System.out.println(">>> [TRAFFIC-SERVICE] ANOMALY DETECTED: " + type + " from " + packet.getSourceIp());

        TrafficLog log = TrafficLog.builder()
                .sourceIp(packet.getSourceIp())
                .destIp(packet.getDestIp())
                .destPort(packet.getDestPort())
                .sizeBytes(packet.getSizeBytes())
                .build();

        AnomalyReport report = AnomalyReport.builder()
                .anomalyType(type)
                .description(description)
                .trafficLog(log)
                .userId(1)
                .build();
        reportRepository.save(report);

        AnomalyDocument doc = AnomalyDocument.builder()
                .id(report.getId().toString())
                .anomalyType(report.getAnomalyType())
                .description(report.getDescription())
                .sourceIp(log.getSourceIp())
                .sizeBytes(log.getSizeBytes())
                .build();
        searchRepository.save(doc);

        AnomalyEvent event = AnomalyEvent.builder()
                .anomalyType(report.getAnomalyType())
                .description(report.getDescription())
                .sourceIp(log.getSourceIp())
                .sizeBytes(log.getSizeBytes())
                .build();

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, "anomaly.detected", event);
    }
}