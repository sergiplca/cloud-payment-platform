package com.sergiplca.notification_service.fixtures;

import com.sergiplca.notification_service.model.dto.event.PaymentEventDto;
import com.sergiplca.notification_service.model.entity.PaymentNotification;
import com.sergiplca.notification_service.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentNotificationFixtures {

    public static PaymentEventDto getPaymentEventDto(Long paymentId) {

        var paymentEventDto = new PaymentEventDto();
        paymentEventDto.setPaymentId(paymentId);
        paymentEventDto.setAmount(BigDecimal.TEN);
        paymentEventDto.setCurrency("EUR");
        paymentEventDto.setCustomerReference("abc");
        paymentEventDto.setStatus(PaymentStatus.CREATED);
        paymentEventDto.setOrderId(1L);
        paymentEventDto.setCreationTimestamp(Instant.now());

        return paymentEventDto;
    }

    public static PaymentNotification getPaymentNotification(Long id) {

        var paymentNotification = new PaymentNotification();
        paymentNotification.setId(id);
        paymentNotification.setPaymentId(1L);
        paymentNotification.setAmount(BigDecimal.TEN);
        paymentNotification.setCurrency("EUR");
        paymentNotification.setCustomerReference("abc");
        paymentNotification.setStatus(PaymentStatus.CREATED);
        paymentNotification.setCreationTimestamp(Instant.now());
        paymentNotification.setOrderId(1L);

        return paymentNotification;

    }
}
