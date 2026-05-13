package com.sergiplca.payment_service.service.kafka;

public interface KafkaProducer {

    <T> void sendMessage(String topic, T payload, String type);
}
