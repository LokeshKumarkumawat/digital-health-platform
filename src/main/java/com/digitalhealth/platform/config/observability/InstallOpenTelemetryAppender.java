package com.digitalhealth.platform.config.observability;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
@Component
public class InstallOpenTelemetryAppender {
    private final OpenTelemetry openTelemetry;

    InstallOpenTelemetryAppender(OpenTelemetry openTelemetry) {
        this.openTelemetry = openTelemetry;
    }

    @PostConstruct
    void install() {
        OpenTelemetryAppender.install(openTelemetry);
    }
}