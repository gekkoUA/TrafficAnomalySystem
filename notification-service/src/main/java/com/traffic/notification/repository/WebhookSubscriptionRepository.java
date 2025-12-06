package com.traffic.notification.repository;

import com.traffic.notification.model.WebhookSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookSubscriptionRepository extends JpaRepository<WebhookSubscription, Integer> {
}