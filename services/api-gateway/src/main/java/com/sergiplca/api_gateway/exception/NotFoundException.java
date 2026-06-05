package com.sergiplca.api_gateway.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String detail) {
        super(detail);
    }
}
