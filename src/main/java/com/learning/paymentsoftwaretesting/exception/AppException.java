package com.learning.paymentsoftwaretesting.exception;

import com.learning.paymentsoftwaretesting.constant.MessageResponseCode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException {

    private final String code;
    private final HttpStatus httpStatus;

    public AppException(MessageResponseCode messageResponseCode) {
        super(messageResponseCode.getMessage());
        this.code = messageResponseCode.getCode();
        this.httpStatus = messageResponseCode.getHttpStatus();
    }

    public AppException(MessageResponseCode messageResponseCode, @NotNull String placeHolder) {
        super(messageResponseCode.getMessage().formatted(placeHolder).trim());
        this.code = messageResponseCode.getCode();
        this.httpStatus = messageResponseCode.getHttpStatus();
    }

    public AppException(MessageResponseCode messageResponseCode, Exception ex) {
        super(messageResponseCode.getMessage(), ex);
        this.code = messageResponseCode.getCode();
        this.httpStatus = messageResponseCode.getHttpStatus();
    }
}
