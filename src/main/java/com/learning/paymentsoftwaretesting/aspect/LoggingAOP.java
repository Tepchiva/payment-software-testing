package com.learning.paymentsoftwaretesting.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.paymentsoftwaretesting.exception.SuccessResponse;
import io.opentelemetry.api.trace.Span;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Aspect
@Component
@Slf4j
public class LoggingAOP {

    private final ObjectMapper mapper;
    private static final String JSON_PARSE_ERROR = "Jackson failed converting data to JSON String: %s";

    @AfterReturning(
            pointcut = "execution(* com.learning.paymentsoftwaretesting..*(..))"
            +" && !execution(* com.learning.paymentsoftwaretesting.config..*(..))"
            +" && !execution(* com.learning.paymentsoftwaretesting.exception..*(..))"
            , returning = "responseResult"
    )
    public void setTraceResponse(Object responseResult) {
        if (responseResult instanceof ResponseEntity<?> responseEntityInstance && responseEntityInstance.getBody() instanceof SuccessResponse<?> successResponseInstance) {
            successResponseInstance.setTraceId(Span.current().getSpanContext().getTraceId());
        }
    }

    @Around(
            "execution(* com.learning.paymentsoftwaretesting..*(..))"
            +" && !execution(* com.learning.paymentsoftwaretesting.config..*(..))"
            +" && !execution(* com.learning.paymentsoftwaretesting.exception..*(..))"
    )
    public Object logAroundAnyMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String[] parameterNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        Map<String, String> requestData = new HashMap<>();

        /// Loop through each params and extract its name and value
        for (int i = 0; i < parameterNames.length; i++) {
            try {
                String paramName = parameterNames[i];
                if (paramName.equals("codeSignature") || paramName.equals("context") || paramName.equals("typeReference")) {
                    continue;
                }
                String paramValue = mapper.writeValueAsString(joinPoint.getArgs()[i]);
                requestData.put(paramName, paramValue);
            } catch (JsonProcessingException e) {
                log.warn(JSON_PARSE_ERROR.formatted(e.getMessage()));
            }
        }

        logAsJson(joinPoint, requestData, "Enter");

        Object responseData = joinPoint.proceed();
        logAsJson(joinPoint, responseData, "Exit");

        return responseData;
    }

    private void logAsJson(ProceedingJoinPoint joinPoint, Object data, String action) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("action", action);
        logData.put("data", data);
        logData.put("method", joinPoint.getSignature().toShortString());
        logData.put("createdAt", LocalDateTime.now().toString());

        try {
            String jsonData = mapper.writeValueAsString(logData);
            log.info(jsonData);
        } catch (JsonProcessingException e) {
            log.warn(JSON_PARSE_ERROR.formatted(e.getMessage()));
        }
    }

   @Around(
           "execution(* com.learning.paymentsoftwaretesting..*(..))"
           +" && !execution(* com.learning.paymentsoftwaretesting.config..*(..))"
   )
    public Object populateMDCProperties(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get the current span
       MDC.put("traceId", Span.current().getSpanContext().getTraceId());
       MDC.put("spanId", Span.current().getSpanContext().getSpanId());

        Object proceed = joinPoint.proceed();

        // Make sure to remove the trace information from the MDC after the log statement
        MDC.remove("traceId");
        MDC.remove("spanId");
        MDC.remove("parentSpanId");

        return proceed;
    }
}