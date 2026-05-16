package com.program.flow_manager.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.outbox")
public record OutboxProperties(
        long publishIntervalMs,
        int batchSize,
        int maxPublishAttempts
) { }
