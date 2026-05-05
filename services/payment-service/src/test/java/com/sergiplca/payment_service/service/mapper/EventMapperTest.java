package com.sergiplca.payment_service.service.mapper;

import com.sergiplca.payment_service.model.dto.event.EventDto;
import com.sergiplca.payment_service.model.enums.EventType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EventMapperTest {

    private final EventMapper<String> eventMapper = new EventMapper<>();

    @Test
    void givenPayloadWhenCreateEventThenEventIsCorrectlyCreated() {

        String payload = "test-payload";
        EventType eventType = EventType.PAYMENT;
        LocalDateTime beforeCall = LocalDateTime.now();

        EventDto<String> result = eventMapper.createEvent(eventType, payload);

        LocalDateTime afterCall = LocalDateTime.now();

        assertNotNull(result);
        assertEquals(eventType, result.getEventType());
        assertEquals(payload, result.getPayload());
        assertNotNull(result.getEventId());
        UUID.fromString(result.getEventId());
        assertThat(result.getEventTimestamp())
            .isAfterOrEqualTo(beforeCall)
            .isBeforeOrEqualTo(afterCall);
    }
}
