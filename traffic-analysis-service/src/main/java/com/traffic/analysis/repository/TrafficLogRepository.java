package com.traffic.analysis.repository;

import com.traffic.analysis.model.TrafficLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrafficLogRepository extends JpaRepository<TrafficLog, Integer> {
}
