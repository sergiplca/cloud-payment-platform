package com.sergiplca.payment_assistant_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergiplca.payment_assistant_service.model.dto.event.EventDto;
import com.sergiplca.payment_assistant_service.model.dto.event.PaymentEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final EmbeddingService embeddingService;

    @KafkaListener(topics = "${app.kafka.payment-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(EventDto<Object> event) {

        log.info("Received event: {}",  event);

        switch (event.getEventType()) {

            case PAYMENT -> handlePayment(event);
            default -> log.warn("Unknown event type: {}", event.getEventType());
        }
    }

    private void handlePayment(EventDto<Object> eventDto) {

        PaymentEventDto payload = objectMapper.convertValue(eventDto.getPayload(), PaymentEventDto.class);
        embeddingService.embedPayment(payload);
    }
}
