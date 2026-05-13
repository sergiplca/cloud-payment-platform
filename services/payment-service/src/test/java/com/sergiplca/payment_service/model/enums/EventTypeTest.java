package com.sergiplca.payment_service.model.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventTypeTest {

    @Test
    void givenEventTypeWhenGetByItsValueThenCorrectEventTypeIsReturned() {

        assertEquals(EventType.PAYMENT, EventType.findByKey("PAYMENT"));
    }
}
