package com.sergiplca.payment_service.service;

import com.sergiplca.payment_service.model.dto.PaymentRequestDto;
import com.sergiplca.payment_service.model.dto.PaymentResponseDto;
import com.sergiplca.payment_service.model.enums.PaymentStatus;
import com.sergiplca.payment_service.repository.PaymentRepository;
import com.sergiplca.payment_service.service.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public PaymentResponseDto createPayment(PaymentRequestDto requestDto) {

        var payment = paymentMapper.toEntity(requestDto);
        payment.setStatus(PaymentStatus.CREATED);

        var savedPayment = paymentRepository.save(payment);
        return paymentMapper.toResponse(savedPayment);
    }
}
