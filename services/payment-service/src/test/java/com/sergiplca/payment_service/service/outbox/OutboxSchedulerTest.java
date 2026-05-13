package com.sergiplca.payment_service.service.outbox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OutboxSchedulerTest {

    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private OutboxScheduler outboxScheduler;

    @Test
    void givenOutboxSchedulerWhenTaskIsRunThenExpectedMethodIsCalled() {

        doNothing().when(outboxService).processUnsentEvents();

        outboxScheduler.processOutboxEvents();

        verify(outboxService).processUnsentEvents();
    }
}
