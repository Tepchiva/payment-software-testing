package com.learning.paymentsoftwaretesting.exception;

import com.learning.paymentsoftwaretesting.constant.MessageResponseCode;
import io.opentelemetry.api.trace.Span;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler({Throwable.class, AppException.class})
    public ResponseEntity<ErrorResponse> handleAppException(Throwable ex) {
        log.error("An error occurred", ex);
        AppException appException = ex instanceof AppException exInstance ? exInstance : new AppException(MessageResponseCode.INTERNAL_SERVER_ERROR);
        ErrorResponse errorResponse = new ErrorResponse(appException);
        errorResponse.setTraceId(Span.current().getSpanContext().getTraceId());
        return new ResponseEntity<>(errorResponse, appException.getHttpStatus());
    }
}
