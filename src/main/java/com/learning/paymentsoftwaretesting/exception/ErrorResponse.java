package com.learning.paymentsoftwaretesting.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private String code;
    private String message;
    private String traceId;

    public ErrorResponse(AppException ex) {
        this.code = ex.getCode();
        this.message = ex.getMessage();
    }
}
