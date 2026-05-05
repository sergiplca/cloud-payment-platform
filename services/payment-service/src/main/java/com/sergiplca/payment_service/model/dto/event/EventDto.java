package com.sergiplca.payment_service.model.dto.event;

import com.sergiplca.payment_service.model.enums.EventType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventDto<T> {

    private String eventId;
    private EventType eventType;
    private LocalDateTime eventTimestamp;
    private T payload;
}
