package com.program.flow_manager.service.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.program.flow_manager.kafka.dto.ConversionRequestEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxPayloadMapper {

    private final ObjectMapper objectMapper;

    public String serialize(ConversionRequestEvent payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Outbox payload must not be null");
        }
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize outbox payload", exception);
        }
    }

    public ConversionRequestEvent deserialize(String payload) {
        try {
            return objectMapper.readValue(payload, ConversionRequestEvent.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to deserialize outbox payload", exception);
        }
    }
}
