package com.traffic.analysis.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "anomalies")
public class AnomalyDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String anomalyType;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private String sourceIp;

    @Field(type = FieldType.Integer)
    private int sizeBytes;
}
