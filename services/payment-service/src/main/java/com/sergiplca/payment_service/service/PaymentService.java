package com.sergiplca.payment_service.service;

import com.sergiplca.payment_service.client.OrderClient;
import com.sergiplca.payment_service.exception.NotFoundException;
import com.sergiplca.payment_service.model.dto.PaymentRequestDto;
import com.sergiplca.payment_service.model.dto.PaymentResponseDto;
import com.sergiplca.payment_service.model.dto.event.PaymentEventDto;
import com.sergiplca.payment_service.model.enums.PaymentStatus;
import com.sergiplca.payment_service.repository.PaymentRepository;
import com.sergiplca.payment_service.service.mapper.EventMapper;
import com.sergiplca.payment_service.service.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sergiplca.payment_service.model.enums.EventType.PAYMENT;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderClient orderClient;
    private final EventMapper<PaymentEventDto> eventMapper;
    private final KafkaProducerService<PaymentEventDto> kafkaProducerService;

    @Value(value = "${app.kafka.payment-topic}")
    private String paymentTopic;

    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto requestDto) {

        log.info("Creating payment");

        var order = orderClient.getOrder(requestDto.getOrderId());

        if (HttpStatusCode.valueOf(404).equals(order.getStatusCode())) {
            throw new NotFoundException("Order with id " + requestDto.getOrderId() + " was not found");
        }

        var payment = paymentMapper.toEntity(requestDto);
        payment.setStatus(PaymentStatus.CREATED);

        var savedPayment = paymentRepository.save(payment);

        kafkaProducerService.sendMessage(
            paymentTopic, eventMapper.createEvent(PAYMENT, paymentMapper.toEventDto(savedPayment)));

        log.info("Payment with id {} created", savedPayment.getId());

        return paymentMapper.toResponse(savedPayment);
    }
}
