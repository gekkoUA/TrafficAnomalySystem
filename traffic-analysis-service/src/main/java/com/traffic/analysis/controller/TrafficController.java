package com.traffic.analysis.controller;

import com.traffic.analysis.document.AnomalyDocument;
import com.traffic.analysis.model.AnomalyReport;
import com.traffic.analysis.service.TrafficService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/traffic")
@RequiredArgsConstructor
public class TrafficController {

    private final TrafficService trafficService;

    @GetMapping("/analyze")
    public ResponseEntity<String> analyzeTraffic() {
        return ResponseEntity.ok(trafficService.analyzeAndSave());
    }

    @GetMapping("/reports")
    public ResponseEntity<List<AnomalyReport>> getAllReports() {
        return ResponseEntity.ok(trafficService.getAllReports());
    }

    @GetMapping("/search")
    public ResponseEntity<List<AnomalyDocument>> search(@RequestParam String ip) {
        return ResponseEntity.ok(trafficService.searchAnomalies(ip));
    }

    @PostMapping(value = "/reports/{id}/evidence", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadEvidence(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file
    ) {
        String imageUrl = trafficService.uploadEvidence(id, file);
        return ResponseEntity.ok("Image uploaded successfully: " + imageUrl);
    }
}