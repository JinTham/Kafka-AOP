package com.personal.kafka.kafkaInterceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

@Slf4j
public class ExistingProducerInterceptor implements ProducerInterceptor<String, Object> {

    @Override
    public ProducerRecord<String, Object> onSend(ProducerRecord<String, Object> record) {
        // Pass through only
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception != null) {
            log.error("Failed to send message - Topic: {}, Partition: {}, Exception: {}",
                    metadata != null ? metadata.topic() : "unknown",
                    metadata != null ? metadata.partition() : -1,
                    exception.getMessage(), exception);
        }
    }

    @Override
    public void close() {
        // No override
    }

    @Override
    public void configure(Map<String, ?> configs) {
        // No override
    }
}
