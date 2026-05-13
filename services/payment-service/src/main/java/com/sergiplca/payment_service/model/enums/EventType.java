package com.sergiplca.payment_service.model.enums;

import com.sergiplca.payment_service.model.dto.event.PaymentEventDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@RequiredArgsConstructor
public enum EventType {

    PAYMENT("PAYMENT", PaymentEventDto.class);

    @Getter
    private final String value;

    @Getter
    private final Class<?> classType;

    private static final Map<String, EventType> eventTypeMap = new HashMap<>();

    static {

        Stream.of(EventType.values()).forEach(eventType -> eventTypeMap.put(eventType.value, eventType));
    }

    public static EventType findByKey(String eventType) {

        return eventTypeMap.get(eventType);
    }
}
