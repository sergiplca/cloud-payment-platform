package com.sergiplca.payment_service.service.mapper;

import com.sergiplca.payment_service.model.dto.event.EventDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class EventMapperImpl implements EventMapper<EventDto<?>> {

    @Override
    public <T> EventDto<T> createEvent(String eventType, T payload) {

        var eventDto = new EventDto<T>();
        eventDto.setEventId(UUID.randomUUID().toString());
        eventDto.setEventType(eventType);
        eventDto.setEventTimestamp(LocalDateTime.now());
        eventDto.setPayload(payload);

        return eventDto;
    }
}
