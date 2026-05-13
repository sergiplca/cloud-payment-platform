package com.sergiplca.payment_service.service.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxService outboxService;

    @Scheduled(fixedDelayString = "${app.outbox.fixed-delay:2000}")
    public void processOutboxEvents() {

        try {
            log.debug("Started scheduled task to process unsent outbox events");

            outboxService.processUnsentEvents();

            log.debug("Ended scheduled task to process unsent outbox events");
        } catch (Exception e) {
            log.error("Error during outbox processing", e);
        }
    }
}
