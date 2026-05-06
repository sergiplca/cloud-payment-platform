package com.sergiplca.notification_service.service.mapper;

import com.sergiplca.notification_service.model.dto.event.PaymentEventDto;
import com.sergiplca.notification_service.model.entity.PaymentNotification;
import com.sergiplca.notification_service.model.enums.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static com.sergiplca.notification_service.fixtures.PaymentNotificationFixtures.getPaymentEventDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentNotificationMapperTest {

    private final PaymentNotificationMapper paymentNotificationMapper =
        Mappers.getMapper(PaymentNotificationMapper.class);

    @Test
    void givenPaymentRequestDtoWhenMapToPaymentThenAllFieldsAreCorrect() {

        PaymentEventDto dto = getPaymentEventDto(1L);

        PaymentNotification entity = paymentNotificationMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(BigDecimal.TEN, entity.getAmount());
        assertEquals("EUR", entity.getCurrency());
        assertEquals("abc", entity.getCustomerReference());
        assertEquals(1L, entity.getOrderId());
        assertNull(entity.getId());
        assertEquals(PaymentStatus.CREATED, entity.getStatus());
        assertNotNull(entity.getCreationTimestamp());
    }
}
