package com.sergiplca.payment_assistant_service.model.dto.event;

import com.sergiplca.payment_assistant_service.model.enums.PaymentStatus;
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
    private Long userId;
    private Long orderId;
    private Instant creationTimestamp;

    public String toEmbeddingText() {

        return String.format(
            "Payment %d: amount=%s %s, status=%s, customer=%s, order=%d, timestamp=%s",
            paymentId, amount.toPlainString(), currency, status.name(), customerReference, orderId, creationTimestamp);
    }

}
