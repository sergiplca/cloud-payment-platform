package com.sergiplca.payment_service.service;

import com.sergiplca.payment_service.model.dto.PaymentRequestDto;
import com.sergiplca.payment_service.model.dto.PaymentResponseDto;
import com.sergiplca.payment_service.model.enums.PaymentStatus;
import com.sergiplca.payment_service.repository.PaymentRepository;
import com.sergiplca.payment_service.service.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto requestDto) {

        log.info("Creating payment");

        var payment = paymentMapper.toEntity(requestDto);
        payment.setStatus(PaymentStatus.CREATED);

        var savedPayment = paymentRepository.save(payment);

        log.info("Payment with id {} created", savedPayment.getId());

        return paymentMapper.toResponse(savedPayment);
    }
}
