package com.traffic.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.traffic.notification.config.RabbitConfig;
import com.traffic.notification.model.AnomalyEvent;
import com.traffic.notification.model.WebhookSubscription;
import com.traffic.notification.repository.WebhookSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final WebhookSubscriptionRepository subscriptionRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final SimpMessagingTemplate messagingTemplate;

    // Додаємо інструмент для ручної конвертації в JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void consumeMessage(AnomalyEvent event) {
        System.out.println(">>> [NOTIFICATION-SERVICE] Received event from RabbitMQ: " + event.getAnomalyType());

        List<WebhookSubscription> subscriptions = subscriptionRepository.findAll();

        for (WebhookSubscription sub : subscriptions) {
            try {
                // 1. Конвертуємо об'єкт у суцільний рядок (це вирішує проблему ESP32)
                String jsonPayload = objectMapper.writeValueAsString(event);

                // 2. Вказуємо правильні заголовки
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                // 3. Збираємо запит
                HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);

                // 4. Відправляємо
                restTemplate.postForEntity(sub.getUrl(), request, Void.class);

            } catch (Exception e) {
                System.err.println("Failed to send webhook: " + e.getMessage());
            }
        }

        messagingTemplate.convertAndSend("/topic/alerts", event);
        System.out.println(">>> [NOTIFICATION-SERVICE] Pushed to WebSocket /topic/alerts");
    }
}