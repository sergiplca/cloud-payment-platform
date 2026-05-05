package com.sergiplca.payment_service.service;

import com.sergiplca.payment_service.model.dto.event.EventDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, EventDto<?>> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService<String> kafkaProducerService;

    @Test
    void shouldSendMessageToKafka() {

        String topic = "test-topic";
        EventDto<String> event = new EventDto<>();
        event.setEventId("123");
        event.setPayload("test");

        kafkaProducerService.sendMessage(topic, event);

        verify(kafkaTemplate, times(1)).send(topic, event);
    }
}
