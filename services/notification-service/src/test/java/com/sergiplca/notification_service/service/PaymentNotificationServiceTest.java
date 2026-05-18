package com.sergiplca.notification_service.service;

import com.sergiplca.notification_service.repository.PaymentNotificationRepository;
import com.sergiplca.notification_service.service.mapper.PaymentNotificationMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static com.sergiplca.notification_service.fixtures.PaymentNotificationFixtures.getPaymentEventDto;
import static com.sergiplca.notification_service.fixtures.PaymentNotificationFixtures.getPaymentNotification;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
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

        verify(paymentNotificationRepository).saveAndFlush(paymentNotification);
    }

    @Test
    void givenAlreadyExistingNotificationForPaymentWhenProcessThenPaymentIsIgnored(CapturedOutput output) {

        var eventDto = getPaymentEventDto(1L);
        var paymentNotification = getPaymentNotification(1L);

        when(paymentNotificationMapper.toEntity(eventDto)).thenReturn(paymentNotification);
        when(paymentNotificationRepository.saveAndFlush(paymentNotification))
            .thenThrow(new DataIntegrityViolationException("ex"));

        paymentNotificationService.processPayment(eventDto);

        assertThat(output).contains("Payment with id 1 already persisted, ignoring payload");
        assertThat(output).doesNotContain("Successfully persisted payment");
    }
}
