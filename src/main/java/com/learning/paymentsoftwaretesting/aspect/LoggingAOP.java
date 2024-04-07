package com.learning.paymentsoftwaretesting.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Aspect
@Component
@Slf4j
public class LoggingAOP {

    private final Tracer tracer;

    @Around("execution(* com.learning.paymentsoftwaretesting..*(..)) && !execution(* com.learning.paymentsoftwaretesting.config..*(..))")
    public Object logAroundAnyMethods(ProceedingJoinPoint joinPoint) throws Throwable{
        Span span = tracer.currentSpan();
        if (span != null) {
            MDC.put("TRACE_ID", Objects.requireNonNull(tracer.currentTraceContext().context()).spanId());

            this.logRequest(joinPoint);
            // Execute method
            Object result = joinPoint.proceed();

            this.logResponse(joinPoint, result);

            return result;
        }
        else {
            return joinPoint.proceed();
        }
    }

    private void logRequest(ProceedingJoinPoint joinPoint) {
        String[] parameterNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
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
                log.error("Jackson failed converting data to JSON String: " + e.getMessage());
            }
        }

        /// Prepare data into "request"
        Map<String, Object> logData = new HashMap<>();
        logData.put("action", "Enter");
        logData.put("requestData", requestData);
        logData.put("method", joinPoint.getSignature().toShortString());
        logData.put("createdAt", LocalDateTime.now().toString());


        /// Log info before execute method
        try {
            String jsonData = mapper.writeValueAsString(logData);
            log.info(jsonData);
        } catch (JsonProcessingException e) {
            log.error("Jackson failed converting data to JSON String: " + e.getMessage());
        }
    }

    private void logResponse(ProceedingJoinPoint joinPoint, Object result) {
        /// Log info after execute method
        Map<String, Object> logData = new HashMap<>();
        logData.put("action", "Exit");
        logData.put("responseData", result);
        logData.put("method", joinPoint.getSignature().toShortString());
        logData.put("createdAt", LocalDateTime.now().toString());

        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            String jsonData = mapper.writeValueAsString(logData);
            log.info(jsonData);
        } catch (JsonProcessingException e) {
            log.error("Jackson failed converting data to JSON String: " + e.getMessage());
        }
    }
}