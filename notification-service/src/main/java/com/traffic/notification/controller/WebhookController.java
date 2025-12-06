package com.traffic.notification.controller;

import com.traffic.notification.model.WebhookSubscription;
import com.traffic.notification.repository.WebhookSubscriptionRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookSubscriptionRepository repository;

    @PostMapping("/subscribe")
    public ResponseEntity<WebhookSubscription> subscribe(@RequestBody UrlRequest request) {
        WebhookSubscription sub = WebhookSubscription.builder().url(request.getUrl()).build();
        return ResponseEntity.ok(repository.save(sub));
    }

    // Mock endpoint for testing
    @PostMapping("/external-monitor-mock")
    public ResponseEntity<String> receiveAlert(@RequestBody Object alertData) {
        System.out.println(">>> [EXTERNAL SYSTEM] Received Webhook Alert: " + alertData);
        return ResponseEntity.ok("Received");
    }

    @Data
    static class UrlRequest { private String url; }
}