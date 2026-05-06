package com.sergiplca.payment_service.fixtures;

import com.sergiplca.payment_service.model.dto.PaymentRequestDto;
import com.sergiplca.payment_service.model.dto.PaymentResponseDto;
import com.sergiplca.payment_service.model.dto.event.PaymentEventDto;
import com.sergiplca.payment_service.model.entity.Payment;
import com.sergiplca.payment_service.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentFixtures {

    public static Payment getPayment(Long paymentId) {

        var payment = new Payment();
        payment.setId(paymentId);
        payment.setAmount(BigDecimal.TEN);
        payment.setCurrency("EUR");
        payment.setCustomerReference("abc");
        payment.setStatus(PaymentStatus.CREATED);
        payment.setCreationTimestamp(Instant.now());
        payment.setOrderId(1L);

        return payment;
    }

    public static PaymentRequestDto getPaymentRequestDto() {

        return getPaymentRequestDto(BigDecimal.TEN, "EUR", "abc", 1L);
    }

    public static PaymentRequestDto getPaymentRequestDto(
        BigDecimal amount, String currency, String customerReference, Long orderId) {

        var requestDto = new PaymentRequestDto();
        requestDto.setAmount(amount);
        requestDto.setCurrency(currency);
        requestDto.setCustomerReference(customerReference);
        requestDto.setOrderId(orderId);

        return requestDto;
    }

    public static PaymentResponseDto getPaymentResponseDto(Long paymentId) {

        var responseDto = new PaymentResponseDto();
        responseDto.setPaymentId(paymentId);
        responseDto.setStatus(PaymentStatus.CREATED);

        return responseDto;
    }

    public static PaymentEventDto getPaymentEventDto(Long paymentId) {

        var eventDto = new PaymentEventDto();
        eventDto.setPaymentId(paymentId);
        eventDto.setAmount(BigDecimal.TEN);
        eventDto.setCurrency("EUR");
        eventDto.setCustomerReference("abc");
        eventDto.setStatus(PaymentStatus.CREATED);
        eventDto.setOrderId(1L);

        return eventDto;
    }
}
