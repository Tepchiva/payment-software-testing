package com.learning.paymentsoftwaretesting.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;

public class SpanLogsAppender extends AppenderBase<ILoggingEvent> {

    /**
     * This is called only for configured levels.
     * It will not be executed for DEBUG level if root logger is INFO.
     */
    @Override
    protected void append(ILoggingEvent event) {
        final Span currentSpan = Span.current();
        AttributesBuilder builder = Attributes.builder();

        if (currentSpan != null) {
            builder.put("logger", event.getLoggerName())
                    .put("level", event.getLevel().toString())
                    .put("message", event.getFormattedMessage());

            currentSpan.addEvent("LogEvent", builder.build());

            if (Level.ERROR.equals(event.getLevel())) {
                currentSpan.setStatus(StatusCode.ERROR);
            }

            IThrowableProxy throwableProxy = event.getThrowableProxy();
            if (throwableProxy instanceof ThrowableProxy throwableProxyInstance) {
                Throwable throwable = throwableProxyInstance.getThrowable();
                if (throwable != null) {
                    currentSpan.recordException(throwable);
                }
            }
        }
    }
}