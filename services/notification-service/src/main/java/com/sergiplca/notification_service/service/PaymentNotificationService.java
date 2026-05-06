package com.sergiplca.notification_service.service;

import com.sergiplca.notification_service.model.dto.event.PaymentEventDto;
import com.sergiplca.notification_service.repository.PaymentNotificationRepository;
import com.sergiplca.notification_service.service.mapper.PaymentNotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentNotificationService {

    private final PaymentNotificationRepository paymentNotificationRepository;
    private final PaymentNotificationMapper paymentNotificationMapper;

    @Transactional
    public void processPayment(PaymentEventDto paymentEventDto) {

        paymentNotificationRepository.save(paymentNotificationMapper.toEntity(paymentEventDto));

        log.info("Successfully persisted payment: {}", paymentEventDto);
    }
}
