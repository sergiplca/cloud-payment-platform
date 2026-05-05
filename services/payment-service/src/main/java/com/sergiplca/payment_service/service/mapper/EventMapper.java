package com.sergiplca.payment_service.service.mapper;

import com.sergiplca.payment_service.model.dto.event.EventDto;
import com.sergiplca.payment_service.model.enums.EventType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class EventMapper<T> {

    public EventDto<T> createEvent(EventType eventType, T payload) {

        var eventDto = new EventDto<T>();
        eventDto.setEventId(UUID.randomUUID().toString());
        eventDto.setEventType(eventType);
        eventDto.setEventTimestamp(LocalDateTime.now());
        eventDto.setPayload(payload);

        return eventDto;
    }
}
