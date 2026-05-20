package com.program.flow_manager.service.outbox;

import com.program.flow_manager.config.properties.KafkaTopicsProperties;
import com.program.flow_manager.config.properties.OutboxProperties;
import com.program.flow_manager.domain.dao.OutboxEventRepository;
import com.program.flow_manager.domain.model.OutboxEventEntity;
import com.program.flow_manager.domain.model.OutboxStatus;
import com.program.flow_manager.kafka.dto.ConversionRequestEvent;
import com.program.flow_manager.kafka.producer.ConversionRequestProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxPublisherService {

    private final OutboxEventRepository outboxRepository;
    private final OutboxService outboxService;
    private final ConversionRequestProducer conversionRequestProducer;
    private final OutboxProperties outboxProperties;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    @Scheduled(fixedDelayString = "${app.outbox.publish-interval-ms}")
    @SchedulerLock(name = "flowManagerOutboxPublisher", lockAtLeastFor = "1s", lockAtMostFor = "30s")
    @Transactional
    public void publishPendingEvents() {

        List<OutboxEventEntity> pendingEvents = outboxRepository.findNextBatchForUpdate(
                OutboxStatus.PENDING.name(),
                outboxProperties.batchSize()
        );

        for (OutboxEventEntity event : pendingEvents) {
            try {
                ConversionRequestEvent payload = outboxService.deserializePayload(event);
                conversionRequestProducer.send(
                        kafkaTopicsProperties.inputTopic(),
                        event.getPartitionKey(),
                        payload,
                        "outbox event " + event.getId()
                );
                outboxService.markSent(event);
            } catch (Exception exception) {
                log.error("Failed to publish outbox event {}", event.getId(), exception);
                outboxService.registerPublishError(
                        event,
                        exception.getMessage(),
                        outboxProperties.maxPublishAttempts(),
                        outboxProperties.publishIntervalMs()
                );
            }
        }
    }
}
