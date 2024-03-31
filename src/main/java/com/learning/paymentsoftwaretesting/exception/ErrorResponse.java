package com.learning.paymentsoftwaretesting.exception;

public record ErrorResponse(String code, String message, String traceId) {
    ErrorResponse(AppException ex) {
        this(ex.getCode(), ex.getMessage(), null);
    }
}
