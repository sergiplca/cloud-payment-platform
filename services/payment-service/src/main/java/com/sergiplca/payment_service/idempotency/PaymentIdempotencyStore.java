package com.sergiplca.payment_service.idempotency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergiplca.payment_service.exception.IdempotencyStoreException;
import com.sergiplca.payment_service.model.dto.PaymentIdempotencyEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class PaymentIdempotencyStore {

    private static final String PREFIX = "idempotency:";
    private static final Duration TTL = Duration.ofHours(24);

    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;

    public boolean setIfAbsent(String idempotencyKey, PaymentIdempotencyEntry entry) {

        try {

            String payload = mapper.writeValueAsString(entry);
            Boolean inserted = redis.opsForValue().setIfAbsent(PREFIX + idempotencyKey, payload, TTL);

            return Boolean.TRUE.equals(inserted);
        } catch (Exception e) {

            throw new IdempotencyStoreException("Failed to write idempotency key", e);
        }
    }

    public void update(String idempotencyKey, PaymentIdempotencyEntry entry) {

        try {

            String payload = mapper.writeValueAsString(entry);
            Long ttlMs = redis.getExpire(PREFIX + idempotencyKey, TimeUnit.MILLISECONDS);
            Duration remaining = (ttlMs != null && ttlMs > 0)
                ? Duration.ofMillis(ttlMs)
                : TTL;
            redis.opsForValue().set(PREFIX + idempotencyKey, payload, remaining);
        } catch (Exception e) {

            throw new IdempotencyStoreException("Failed to update idempotency key", e);
        }
    }

    public Optional<PaymentIdempotencyEntry> get(String idempotencyKey) {

        try {

            String json = redis.opsForValue().get(PREFIX + idempotencyKey);
            if (json == null) return Optional.empty();

            return Optional.of(mapper.readValue(json, PaymentIdempotencyEntry.class));
        } catch (Exception e) {

            throw new IdempotencyStoreException("Failed to read idempotency key", e);
        }
    }
}
