package com.sergiplca.payment_service.controller;

import com.sergiplca.payment_service.model.dto.PaymentRequestDto;
import com.sergiplca.payment_service.model.dto.PaymentResponseDto;
import com.sergiplca.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController implements PaymentControllerSwaggerSpec {

    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponseDto createPayment(@Valid @RequestBody PaymentRequestDto requestDto) {

        return paymentService.createPayment(requestDto);
    }
}
