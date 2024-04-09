package com.learning.paymentsoftwaretesting.exception;

import com.learning.paymentsoftwaretesting.constant.MessageResponseCode;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final Tracer tracer;

    @ExceptionHandler({Throwable.class, AppException.class})
    public ResponseEntity<ErrorResponse> handleAppException(Throwable ex) {
        log.error("An error occurred", ex);
        AppException appException = ex instanceof AppException exInstance ? exInstance : new AppException(MessageResponseCode.INTERNAL_SERVER_ERROR);
        ErrorResponse errorResponse = new ErrorResponse(appException);
        Span span = tracer.currentSpan();
        if (span != null) {
            errorResponse.setTraceId(span.context().traceId());
        }
        return new ResponseEntity<>(errorResponse, appException.getHttpStatus());
    }
}
