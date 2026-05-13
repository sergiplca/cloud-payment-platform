package com.sergiplca.payment_service.service.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergiplca.payment_service.configuration.props.OutboxProperties;
import com.sergiplca.payment_service.model.entity.OutboxEvent;
import com.sergiplca.payment_service.model.enums.EventType;
import com.sergiplca.payment_service.repository.OutboxRepository;
import com.sergiplca.payment_service.service.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;
    private final OutboxProperties outboxProperties;

    public void saveOutboxEvent(String payload, String eventType, String destinationTopic) {

        outboxRepository.save(OutboxEvent.builder()
            .payload(payload)
            .eventType(eventType)
            .destinationTopic(destinationTopic)
            .creationTimestamp(Instant.now())
            .build());
    }

    @Transactional
    public void processUnsentEvents() {

        outboxRepository.findAll(
                PageRequest.of(0, outboxProperties.getPageSize(), Sort.by(outboxProperties.getDefaultSortColumn())))
            .forEach(this::processItem);
    }

    @SneakyThrows
    private void processItem(OutboxEvent event) {

        kafkaProducer.sendMessage(
            event.getDestinationTopic(),
            objectMapper.readValue(event.getPayload(), EventType.findByKey(event.getEventType()).getClassType()),
            event.getEventType());

        outboxRepository.delete(event);

        log.info("Event sent: {}", event);
    }
}
