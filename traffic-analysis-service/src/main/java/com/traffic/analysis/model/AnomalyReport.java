package com.traffic.analysis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "anomaly_reports")
public class AnomalyReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String anomalyType;
    private String description;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private String imageUrl;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "traffic_log_id", referencedColumnName = "id")
    private TrafficLog trafficLog;

    private Integer userId; // Просто ID
}