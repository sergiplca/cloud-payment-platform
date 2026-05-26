package com.sergiplca.payment_service.idempotency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergiplca.payment_service.model.dto.PaymentIdempotencyEntry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Component
@RequiredArgsConstructor
public class PaymentIdempotencyFilter extends OncePerRequestFilter {

    static final String HEADER = "Idempotency-Key";

    private final PaymentIdempotencyStore store;
    private final ObjectMapper mapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        return !("POST".equalsIgnoreCase(request.getMethod())
            && request.getRequestURI().matches("^/v1/payments$"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

        String idempotencyKey = request.getHeader(HEADER);
        var cachedRequest  = new ContentCachingRequestWrapper(request, 0);
        var cachedResponse = new ContentCachingResponseWrapper(response);

        if (idempotencyKey == null) {

            sendError(response, HttpStatus.BAD_REQUEST, "Request unprocessable: Idempotency-Key header is missing");
            return;
        }

        byte[] bodyBytes = cachedRequest.getContentAsByteArray();
        String requestHash = sha256(bodyBytes);

        var existingEntry = store.get(idempotencyKey);

        if (existingEntry.isPresent()) {

            var entry = existingEntry.get();

            if (!entry.requestHash().equals(requestHash)) {
                sendError(response, HttpStatus.UNPROCESSABLE_CONTENT,
                    "Idempotency key reused with a different request body");

                return;
            }

            if (entry.isProcessing()) {
                sendError(response, HttpStatus.CONFLICT,
                    "A request with this idempotency key is already being processed");

                return;
            }

            response.setStatus(entry.responseStatus());
            response.setContentType("application/json");
            response.getWriter().write(entry.response());
            response.addHeader("Idempotent-Replayed", "true");

            return;
        }

        PaymentIdempotencyEntry newEntry = PaymentIdempotencyEntry.processing(requestHash);
        boolean claimed = store.setIfAbsent(idempotencyKey, newEntry);

        if (!claimed) {
            sendError(response, HttpStatus.CONFLICT,
                "A request with this idempotency key is already being processed");

            return;
        }

        chain.doFilter(cachedRequest, cachedResponse);

        String responseBody   = new String(cachedResponse.getContentAsByteArray(), StandardCharsets.UTF_8);
        int responseStatus = cachedResponse.getStatus();
        store.update(idempotencyKey, newEntry.complete(responseBody, responseStatus));

        cachedResponse.copyBodyToResponse();
    }

    private String sha256(byte[] data) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            return HexFormat.of().formatHex(digest.digest(data));
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 unavailable", e);
        }
    }

    private void sendError(HttpServletResponse response, HttpStatus status, String message) {

        try {
            response.setStatus(status.value());
            response.setContentType("application/json");
            response.getWriter().write(mapper.writeValueAsString(new ErrorResponse(status.value(), message)));
        } catch (IOException e) {

            throw new RuntimeException("Error while writing error to response", e);
        }
    }

    record ErrorResponse(int status, String message) {}
}
