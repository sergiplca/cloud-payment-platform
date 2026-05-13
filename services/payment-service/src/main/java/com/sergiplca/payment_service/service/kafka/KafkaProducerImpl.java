package com.sergiplca.payment_service.service.kafka;

import com.sergiplca.payment_service.service.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerImpl<E> implements KafkaProducer {

    private final EventMapper<E> eventMapper;
    private final KafkaTemplate<String, E> kafkaTemplate;

    @Override
    public <T> void sendMessage(String topic, T eventDto, String type) {

        kafkaTemplate.send(topic, eventMapper.createEvent(type, eventDto));
        log.info("Message sent to topic {}: {}", topic, eventDto);
    }
}
