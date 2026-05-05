package com.sergiplca.payment_service.service;

import com.sergiplca.payment_service.model.dto.event.EventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService<T> {

    private final KafkaTemplate<String, EventDto<?>> kafkaTemplate;

    public void sendMessage(String topic, EventDto<T> eventDto) {

        kafkaTemplate.send(topic, eventDto);
        log.info("Message sent to topic {}: {}", topic, eventDto);
    }
}
