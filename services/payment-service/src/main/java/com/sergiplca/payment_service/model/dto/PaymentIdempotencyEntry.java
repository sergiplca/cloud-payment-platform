package com.sergiplca.payment_service.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;

public record PaymentIdempotencyEntry(
    String status, String requestHash, String response, int responseStatus, Instant createdAt) {

    public static PaymentIdempotencyEntry processing(String requestHash) {

        return new PaymentIdempotencyEntry("processing", requestHash, null, 0, Instant.now());
    }

    public PaymentIdempotencyEntry complete(String response, int responseStatus) {

        return new PaymentIdempotencyEntry("complete", this.requestHash, response, responseStatus, this.createdAt);
    }

    @JsonIgnore
    public boolean isProcessing() {

        return "processing".equals(status);
    }
}
