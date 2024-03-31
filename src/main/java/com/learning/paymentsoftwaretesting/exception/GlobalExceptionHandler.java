package com.learning.paymentsoftwaretesting.exception;

import com.learning.paymentsoftwaretesting.constant.MessageResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex);
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleException(Throwable ex) {
        log.error("Internal server error", ex);
        AppException appException = new AppException(MessageResponseCode.ERROR);
        ErrorResponse errorResponse = new ErrorResponse(appException);
        return new ResponseEntity<>(errorResponse, appException.getHttpStatus());
    }
}
