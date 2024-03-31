package com.learning.paymentsoftwaretesting.exception;

public record SuccessResponse<T>(String code, String message, String traceId, T data) {}
