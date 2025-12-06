package com.traffic.notification.service;

import com.traffic.notification.config.RabbitConfig;
import com.traffic.notification.model.AnomalyEvent;
import com.traffic.notification.model.WebhookSubscription;
import com.traffic.notification.repository.WebhookSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate; // Для WebSocket
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final WebhookSubscriptionRepository subscriptionRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void consumeMessage(AnomalyEvent event) {
        System.out.println(">>> [NOTIFICATION-SERVICE] Received event from RabbitMQ: " + event.getAnomalyType());

        List<WebhookSubscription> subscriptions = subscriptionRepository.findAll();
        for (WebhookSubscription sub : subscriptions) {
            try {
                restTemplate.postForEntity(sub.getUrl(), event, Void.class);
            } catch (Exception e) {
                System.err.println("Failed to send webhook: " + e.getMessage());
            }
        }

        messagingTemplate.convertAndSend("/topic/alerts", event);
        System.out.println(">>> [NOTIFICATION-SERVICE] Pushed to WebSocket /topic/alerts");
    }
}