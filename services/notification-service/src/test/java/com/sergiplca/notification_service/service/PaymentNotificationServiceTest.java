package com.sergiplca.notification_service.service;

import com.sergiplca.notification_service.repository.PaymentNotificationRepository;
import com.sergiplca.notification_service.service.mapper.PaymentNotificationMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.sergiplca.notification_service.fixtures.PaymentNotificationFixtures.getPaymentEventDto;
import static com.sergiplca.notification_service.fixtures.PaymentNotificationFixtures.getPaymentNotification;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentNotificationServiceTest {

    @Mock
    private PaymentNotificationRepository paymentNotificationRepository;

    @Mock
    private PaymentNotificationMapper paymentNotificationMapper;

    @InjectMocks
    private PaymentNotificationService paymentNotificationService;

    @Test
    void givenPaymentEventDtoWhenInvokeServiceThenActionsArePerformed() {

        var eventDto = getPaymentEventDto(1L);
        var paymentNotification = getPaymentNotification(1L);

        when(paymentNotificationMapper.toEntity(eventDto)).thenReturn(paymentNotification);

        paymentNotificationService.processPayment(eventDto);

        verify(paymentNotificationRepository).save(paymentNotification);
    }
}
