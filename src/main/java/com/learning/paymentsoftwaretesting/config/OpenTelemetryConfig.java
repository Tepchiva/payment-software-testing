/*

package com.learning.paymentsoftwaretesting.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.sdk.logs.LogRecordProcessor;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.SdkLoggerProviderBuilder;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.semconv.ResourceAttributes;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// Currently, not use lockback for logging, use log4j2 instead
// If want to use it, need to add logback-spring.xml in resources, and add this @configuration

<dependency>
    <groupId>io.opentelemetry.instrumentation</groupId>
    <artifactId>opentelemetry-logback-appender-1.0</artifactId>
    <version>2.2.0-alpha</version>
</dependency>
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    <include resource="org/springframework/boot/logging/logback/console-appender.xml" />
    <appender name="OPEN_TELEMETRY"
              class="io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender">
        <captureExperimentalAttributes>true</captureExperimentalAttributes>
        <captureKeyValuePairAttributes>true</captureKeyValuePairAttributes>
    </appender>
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="OPEN_TELEMETRY"/>
    </root>
</configuration>

@Configuration
public class OpenTelemetryConfig {
    @Bean
    OpenTelemetry openTelemetry(SdkLoggerProvider sdkLoggerProvider, SdkTracerProvider sdkTracerProvider, ContextPropagators contextPropagators) {
        OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder()
                .setLoggerProvider(sdkLoggerProvider)
                .setTracerProvider(sdkTracerProvider)
                .setPropagators(contextPropagators)
                .build();

        OpenTelemetryAppender.install(openTelemetrySdk);
        return openTelemetrySdk;
    }

    @Bean
    SdkLoggerProvider otelSdkLoggerProvider(@Value("${spring.application.name}") String applicationName, ObjectProvider<LogRecordProcessor> logRecordProcessors) {
        Resource springResource = Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, applicationName));
        SdkLoggerProviderBuilder builder = SdkLoggerProvider
                .builder()
                .setResource(Resource.getDefault().merge(springResource));
        logRecordProcessors.orderedStream().forEach(builder::addLogRecordProcessor);
        return builder.build();
    }

    @Bean
    LogRecordProcessor otelLogRecordProcessor(@Value("${grpc.exporter.url}") String url) {
        return BatchLogRecordProcessor
                .builder(
                        OtlpGrpcLogRecordExporter
                                .builder()
                                .setEndpoint(url)
                                .build()
                )
                .build();
    }
}


 */