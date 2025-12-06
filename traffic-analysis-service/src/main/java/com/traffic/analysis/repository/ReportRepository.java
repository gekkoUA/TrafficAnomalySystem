package com.traffic.analysis.repository;

import com.traffic.analysis.model.AnomalyReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<AnomalyReport, Integer> {
}