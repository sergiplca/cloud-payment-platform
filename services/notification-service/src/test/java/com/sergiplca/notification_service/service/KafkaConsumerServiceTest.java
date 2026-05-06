package com.sergiplca.notification_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergiplca.notification_service.model.dto.event.EventDto;
import com.sergiplca.notification_service.model.dto.event.PaymentEventDto;
import com.sergiplca.notification_service.model.enums.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    @Mock
    private PaymentNotificationService paymentNotificationService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    private EventDto<Object> eventDto;
    private PaymentEventDto paymentEventDto;

    @BeforeEach
    void setUp() {

        eventDto = new EventDto<>();
        paymentEventDto = new PaymentEventDto();
    }

    @Test
    void givenPaymentEventWhenProcessThenAllGoesSuccessfully() {

        Object rawPayload = new Object();
        eventDto.setEventType(EventType.PAYMENT);
        eventDto.setPayload(rawPayload);

        when(objectMapper.convertValue(rawPayload, PaymentEventDto.class)).thenReturn(paymentEventDto);

        kafkaConsumerService.consume(eventDto);

        verify(objectMapper).convertValue(rawPayload, PaymentEventDto.class);
        verify(paymentNotificationService).processPayment(paymentEventDto);
    }

    @Test
    void givenUnknownEventTypeWhenProcessThenNothingIsCalled() {

        eventDto.setEventType(EventType.UNKNOWN);

        kafkaConsumerService.consume(eventDto);

        verifyNoInteractions(paymentNotificationService);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void givenKnownEventWithNullPayloadWhenProcessThenItIsGracefullyProcessed() {

        eventDto.setEventType(EventType.PAYMENT);
        eventDto.setPayload(null);

        when(objectMapper.convertValue(null, PaymentEventDto.class)).thenReturn(paymentEventDto);

        kafkaConsumerService.consume(eventDto);

        verify(objectMapper).convertValue(null, PaymentEventDto.class);
        verify(paymentNotificationService).processPayment(paymentEventDto);
    }
}
