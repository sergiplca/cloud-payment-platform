package com.sergiplca.payment_service.model.dto.event;

import com.sergiplca.payment_service.model.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentEventDto {

    private Long paymentId;
    private BigDecimal amount;
    private String currency;
    private String customerReference;
    private PaymentStatus status;
    private Long orderId;
}
