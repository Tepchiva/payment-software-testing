package com.learning.paymentsoftwaretesting.exception;

import com.learning.paymentsoftwaretesting.constant.MessageResponseCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SuccessResponse<T> {
    private String code;
    private String message;
    private String traceId;
    private T data;

    public SuccessResponse(T data) {
        this.code = MessageResponseCode.SUCCESS.getCode();
        this.message = MessageResponseCode.SUCCESS.getCode();
        this.traceId = null;
        this.data = data;
    }
}