package com.sergiplca.payment_service.model.entity;

import com.sergiplca.payment_service.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_seq")
    @SequenceGenerator(name = "payment_seq", sequenceName = "paymentservice.payment_sequence", allocationSize = 1)
    private Long id;

    private BigDecimal amount;

    private String currency;

    private String customerReference;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @CreationTimestamp
    private LocalDateTime creationTimestamp;
}
