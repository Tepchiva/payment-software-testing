package com.learning.paymentsoftwaretesting.exception;

import com.learning.paymentsoftwaretesting.constant.MessageResponseCode;

public record SuccessResponse<T>(String code, String message, String traceId, T data) {
    public SuccessResponse(T data) {
        this(MessageResponseCode.SUCCESS.getCode(), MessageResponseCode.SUCCESS.getCode(), null, data);
    }
}
