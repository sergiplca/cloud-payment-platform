package com.sergiplca.payment_service.service.mapper;

import com.sergiplca.payment_service.model.dto.PaymentRequestDto;
import com.sergiplca.payment_service.model.dto.PaymentResponseDto;
import com.sergiplca.payment_service.model.dto.event.PaymentEventDto;
import com.sergiplca.payment_service.model.entity.Payment;
import com.sergiplca.payment_service.model.enums.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static com.sergiplca.payment_service.fixtures.PaymentFixtures.getPayment;
import static com.sergiplca.payment_service.fixtures.PaymentFixtures.getPaymentRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentMapperTest {

    private final PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);

    @Test
    void givenPaymentRequestDtoWhenMapToPaymentThenAllFieldsAreCorrect() {

        PaymentRequestDto dto = getPaymentRequestDto();

        Payment entity = paymentMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(BigDecimal.TEN, entity.getAmount());
        assertEquals("EUR", entity.getCurrency());
        assertEquals("abc", entity.getCustomerReference());
        assertEquals(1L, entity.getOrderId());
        assertNull(entity.getId());
        assertNull(entity.getStatus());
        assertNotNull(entity.getCreationTimestamp());
    }

    @Test
    void givenPaymentWhenMapToPaymentResponseDtoThenAllFieldsAreCorrect() {

        Payment entity = getPayment(1L);

        PaymentResponseDto responseDto = paymentMapper.toResponse(entity);

        assertNotNull(responseDto);
        assertEquals(1L, responseDto.getPaymentId());
        assertEquals(PaymentStatus.CREATED, responseDto.getStatus());
    }

    @Test
    void givenPaymentWhenMapToPaymentEventDtoThenAllFieldsAreCorrect() {

        Payment entity = getPayment(1L);

        PaymentEventDto eventDto = paymentMapper.toEventDto(entity);

        assertNotNull(eventDto);
        assertEquals(1L, eventDto.getPaymentId());
        assertEquals(PaymentStatus.CREATED, eventDto.getStatus());
        assertEquals(BigDecimal.TEN, eventDto.getAmount());
        assertEquals("EUR", eventDto.getCurrency());
        assertEquals("abc", eventDto.getCustomerReference());
        assertEquals(1L, eventDto.getOrderId());
    }
}
