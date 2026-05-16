package com.program.flow_manager.kafka.consumer;

import com.program.flow_manager.domain.dao.ConversionTaskRepository;
import com.program.flow_manager.domain.model.ConversionTaskEntity;
import com.program.flow_manager.exception.ConversionTaskNotFoundException;
import com.program.flow_manager.kafka.dto.ConversionResultEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConversionResultListener {

    private final ConversionTaskRepository conversionTaskRepository;

    @KafkaListener(topics = "${app.kafka.output-topic}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void consume(ConversionResultEvent event, Acknowledgment acknowledgment) {
        validateEvent(event);

        ConversionTaskEntity task = conversionTaskRepository.findById(event.taskId())
                .orElseThrow(() -> new ConversionTaskNotFoundException("Conversion task not found: " + event.taskId()));

        task.setResultBucket(event.resultBucket());
        task.setResultObjectKey(event.resultObjectKey());
        task.setErrorMessage(event.errorMessage());
        task.setStatus(event.status());
        conversionTaskRepository.save(task);
        acknowledgment.acknowledge();
    }

    private void validateEvent(ConversionResultEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Conversion result event must not be null");
        }
        if (event.taskId() == null) {
            throw new IllegalArgumentException("taskId must not be null");
        }
        if (event.sourceBucket() == null || event.sourceBucket().isBlank()) {
            throw new IllegalArgumentException("sourceBucket must not be blank");
        }
        if (event.sourceObjectKey() == null || event.sourceObjectKey().isBlank()) {
            throw new IllegalArgumentException("sourceObjectKey must not be blank");
        }
        if (event.status() == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        switch (event.status()) {
            case SUCCESS -> {
                if (event.resultBucket() == null || event.resultBucket().isBlank()) {
                    throw new IllegalArgumentException("resultBucket must not be blank when status is SUCCESS");
                }
                if (event.resultObjectKey() == null || event.resultObjectKey().isBlank()) {
                    throw new IllegalArgumentException("resultObjectKey must not be blank when status is SUCCESS");
                }
            }
            case FAILED -> {
                if (event.errorMessage() == null || event.errorMessage().isBlank()) {
                    throw new IllegalArgumentException("errorMessage must not be blank when status is FAILED");
                }
            }
            default -> throw new IllegalArgumentException("Unsupported conversion status: " + event.status());
        }
    }
}
