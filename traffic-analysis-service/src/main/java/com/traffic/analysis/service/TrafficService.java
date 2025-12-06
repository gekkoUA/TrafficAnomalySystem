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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TrafficService {

    private final TrafficLogRepository trafficLogRepository;
    private final ReportRepository reportRepository;
    private final S3Service s3Service;
    private final RabbitTemplate rabbitTemplate;

    private final AnomalySearchRepository searchRepository;

    public String analyzeAndSave() {
        System.out.println(">>> [TRAFFIC-SERVICE] Starting traffic generation...");
        PacketSource generator = new RandomPacketGenerator();
        PacketRegistry registry = new PacketRegistry();

        for (int i = 0; i < 50; i++) {
            registry.add(generator.getNextPacket());
        }

        TrafficStatistics stats = new TrafficStatistics();
        List<TrafficPacket> suspiciousPackets = stats.filterAndSortStream(registry, 1000);
        System.out.println(">>> [TRAFFIC-SERVICE] Found " + suspiciousPackets.size() + " anomalies.");

        int savedCount = 0;
        for (TrafficPacket packet : suspiciousPackets) {
            TrafficLog log = TrafficLog.builder()
                    .sourceIp(packet.getSourceIp())
                    .destIp("10.0.0.1")
                    .destPort(packet.getDestPort())
                    .sizeBytes(packet.getSizeBytes())
                    .build();

            AnomalyReport report = AnomalyReport.builder()
                    .anomalyType("High Volume")
                    .description("Packet size " + packet.getSizeBytes() + " exceeds limit")
                    .trafficLog(log)
                    .userId(1)
                    .build();

            reportRepository.save(report);

            AnomalyDocument doc = AnomalyDocument.builder()
                    .id(report.getId().toString()) // ID з бази беремо як ID для Elastic
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
            System.out.println(">>> [TRAFFIC-SERVICE] Processed anomaly for: " + packet.getSourceIp());

            savedCount++;
        }

        return "Analysis Complete. Saved " + savedCount + " anomalies to Postgres & Elasticsearch.";
    }

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
}