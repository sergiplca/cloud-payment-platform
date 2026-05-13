package com.sergiplca.payment_service.service.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergiplca.payment_service.configuration.props.OutboxProperties;
import com.sergiplca.payment_service.model.entity.OutboxEvent;
import com.sergiplca.payment_service.repository.OutboxRepository;
import com.sergiplca.payment_service.service.kafka.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.sergiplca.payment_service.fixtures.OutboxFixtures.getOutboxEvent;
import static com.sergiplca.payment_service.fixtures.PaymentFixtures.getPaymentEventDto;
import static com.sergiplca.payment_service.model.enums.EventType.PAYMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxServiceTest {

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private OutboxProperties outboxProperties;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private OutboxService outboxService;

    @BeforeEach
    void setUp() {

        outboxService = new OutboxService(outboxRepository, kafkaProducer, objectMapper, outboxProperties);
    }

    @Test
    void givenNewOutboxEventWhenSaveThenEventIsSaved() {

        var payload = "payload";
        var eventType = "type";
        var topic = "topic";

        ArgumentCaptor<OutboxEvent> outboxEventCaptor = ArgumentCaptor.forClass(OutboxEvent.class);

        outboxService.saveOutboxEvent(payload, eventType, topic);

        verify(outboxRepository).save(outboxEventCaptor.capture());
        assertEquals(payload, outboxEventCaptor.getValue().getPayload());
        assertEquals(eventType, outboxEventCaptor.getValue().getEventType());
        assertEquals(topic, outboxEventCaptor.getValue().getDestinationTopic());
        assertNotNull(outboxEventCaptor.getValue().getCreationTimestamp());
    }

    @Test
    void givenNoEventsToProcessWhenTaskIsTriggeredThenNothingIsDone() {

        when(outboxProperties.getPageSize()).thenReturn(512);
        when(outboxProperties.getDefaultSortColumn()).thenReturn("creationTimestamp");
        when(outboxRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        outboxService.processUnsentEvents();

        verify(outboxRepository, never()).delete(any());
        verify(kafkaProducer, never()).sendMessage(any(), any(), any());
    }

    @Test
    void givenUnsentPaymentEventsThenEventsAreSentAndDeleted() throws JsonProcessingException {

        var paymentEventDto = getPaymentEventDto(1L);
        var outboxEvents = List.of(
            getOutboxEvent(objectMapper.writeValueAsString(paymentEventDto), PAYMENT.getValue(), "topic"));

        when(outboxProperties.getPageSize()).thenReturn(512);
        when(outboxProperties.getDefaultSortColumn()).thenReturn("creationTimestamp");
        when(outboxRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(outboxEvents));

        outboxService.processUnsentEvents();

        verify(kafkaProducer, times(outboxEvents.size()))
            .sendMessage("topic", paymentEventDto, PAYMENT.getValue());
        verify(outboxRepository, times(outboxEvents.size())).delete(any(OutboxEvent.class));
    }
}
