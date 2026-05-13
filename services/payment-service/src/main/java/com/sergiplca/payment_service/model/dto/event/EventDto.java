package com.sergiplca.payment_service.model.dto.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventDto<T> {

    private String eventId;
    private String eventType;
    private LocalDateTime eventTimestamp;
    private T payload;
}
