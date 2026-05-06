package com.sergiplca.notification_service.model.dto.event;

import com.sergiplca.notification_service.model.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class PaymentEventDto {

    private Long paymentId;
    private BigDecimal amount;
    private String currency;
    private String customerReference;
    private PaymentStatus status;
    private Long orderId;
    private Instant creationTimestamp;
}
