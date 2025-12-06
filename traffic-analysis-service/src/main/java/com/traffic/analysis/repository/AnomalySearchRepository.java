package com.traffic.analysis.repository;

import com.traffic.analysis.document.AnomalyDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface AnomalySearchRepository extends ElasticsearchRepository<AnomalyDocument, String> {
    List<AnomalyDocument> findByAnomalyType(String anomalyType);

    List<AnomalyDocument> findBySourceIp(String sourceIp);
}