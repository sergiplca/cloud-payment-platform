package com.sergiplca.payment_service.service.kafka;

import com.sergiplca.payment_service.model.dto.event.EventDto;
import com.sergiplca.payment_service.service.mapper.EventMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerImplTest {

    @Mock
    private KafkaTemplate<String, EventDto<?>> kafkaTemplate;

    @Mock
    private EventMapper<EventDto<String>> eventMapper;

    @InjectMocks
    private KafkaProducerImpl<String> kafkaProducerImpl;

    @Test
    void shouldSendMessageToKafka() {

        String topic = "test-topic";
        String type = "test-type";
        String payload = "test-payload";
        EventDto<String> event = new EventDto<>();

        when(eventMapper.createEvent(type, payload)).thenReturn(event);

        kafkaProducerImpl.sendMessage(topic, payload, type);

        verify(kafkaTemplate, times(1)).send(topic, event);
    }
}
