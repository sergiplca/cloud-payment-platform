package com.sergiplca.payment_service.exception;

public class IdempotencyStoreException extends RuntimeException {

    public IdempotencyStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
