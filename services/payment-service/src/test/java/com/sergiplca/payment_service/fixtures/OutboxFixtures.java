package com.sergiplca.payment_service.fixtures;

import com.sergiplca.payment_service.model.entity.OutboxEvent;

import java.time.Instant;

public class OutboxFixtures {

    public static OutboxEvent getOutboxEvent(String payload, String type, String topic) {

        var outboxEvent = new OutboxEvent();
        outboxEvent.setPayload(payload);
        outboxEvent.setEventType(type);
        outboxEvent.setDestinationTopic(topic);
        outboxEvent.setCreationTimestamp(Instant.now());

        return outboxEvent;
    }
}
