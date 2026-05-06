package com.sergiplca.notification_service.model.entity;

import com.sergiplca.notification_service.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Data
@Table(name = "payment_notification")
public class PaymentNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_notification_seq")
    @SequenceGenerator(
        name = "payment_notification_seq",
        sequenceName = "notificationservice.payment_notification_sequence",
        allocationSize = 1
    )
    private Long id;

    private Long paymentId;

    private BigDecimal amount;

    private String currency;

    private String customerReference;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private Instant creationTimestamp;

    private Long orderId;
}
