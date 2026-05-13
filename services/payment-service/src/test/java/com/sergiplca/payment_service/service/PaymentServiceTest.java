package com.sergiplca.payment_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergiplca.payment_service.client.OrderClient;
import com.sergiplca.payment_service.exception.NotFoundException;
import com.sergiplca.payment_service.model.dto.event.EventDto;
import com.sergiplca.payment_service.model.dto.event.PaymentEventDto;
import com.sergiplca.payment_service.model.entity.Payment;
import com.sergiplca.payment_service.model.enums.PaymentStatus;
import com.sergiplca.payment_service.repository.PaymentRepository;
import com.sergiplca.payment_service.service.mapper.PaymentMapper;
import com.sergiplca.payment_service.service.outbox.OutboxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static com.sergiplca.payment_service.fixtures.OrderFixtures.getOrderResponseDto;
import static com.sergiplca.payment_service.fixtures.PaymentFixtures.*;
import static com.sergiplca.payment_service.model.enums.EventType.PAYMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private OrderClient orderClient;

    @Mock
    private OutboxService outboxService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {

        paymentService = new PaymentService(paymentRepository, paymentMapper, orderClient, outboxService, objectMapper);
    }

    @Test
    void givenPaymentRequestDtoWhenCreatePaymentThenPaymentIsCreated() throws JsonProcessingException {

        var paymentRequestDto = getPaymentRequestDto();
        var paymentEventDto = getPaymentEventDto(1L);
        var eventDto = new EventDto<PaymentEventDto>();
        eventDto.setPayload(paymentEventDto);

        when(paymentMapper.toEntity(paymentRequestDto)).thenReturn(getPayment(1L));
        when(paymentRepository.save(any())).thenReturn(getPayment(1L));
        when(orderClient.getOrder(1L)).thenReturn(ResponseEntity.ok(getOrderResponseDto(1L)));
        when(paymentMapper.toEventDto(any(Payment.class))).thenReturn(paymentEventDto);
        doNothing().when(outboxService).saveOutboxEvent(any(), any(), any());

        paymentService.createPayment(paymentRequestDto);

        ArgumentCaptor<Payment> orderCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(orderCaptor.capture());
        assertEquals(BigDecimal.TEN, orderCaptor.getValue().getAmount());
        assertEquals("EUR", orderCaptor.getValue().getCurrency());
        assertEquals("abc", orderCaptor.getValue().getCustomerReference());
        assertEquals(PaymentStatus.CREATED, orderCaptor.getValue().getStatus());
        verify(outboxService).saveOutboxEvent(
            objectMapper.writeValueAsString(paymentEventDto), PAYMENT.getValue(), null);
    }

    @Test
    void givenPaymentRequestDtoForNonExistingOrderWhenCreatePaymentThenError() {

        var requestDto = getPaymentRequestDto();

        when(orderClient.getOrder(1L)).thenReturn(ResponseEntity.notFound().build());

        var ex = assertThrows(NotFoundException.class, () -> paymentService.createPayment(requestDto));
        assertEquals("Order with id " + requestDto.getOrderId() + " was not found", ex.getMessage());
    }
}
