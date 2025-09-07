package com.personal.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.personal.kafka.config.KafkaTopicConfig.JIN_TOPIC;

@Slf4j
@Component
public class KafkaListeners {

    // Successful
    @KafkaListener(topics = JIN_TOPIC, groupId = "jin-group")
    void listener(String data) {
        log.info("Listener received: {}", data);
    }

    // Business Logic Exception
    @KafkaListener(topics = "business-fail-topic", groupId = "jin-group")
    void listenerLogicFail(String data) throws Exception {
        log.error("Service throw exception");
        throw new Exception("Service throw exception");
    }

    // Deserialization exception (simulate with invalid type)
    @KafkaListener(topics = "json-fail-topic", groupId = "json-group")
    public void jsonListener(Integer dto) {
        // will fail if invalid JSON is sent
        System.out.println("Received DTO: " + dto);
    }

    // Batch listener
    @KafkaListener(topics = "batch-fail-topic", groupId = "batch-group")
    public void batchListener(List<String> messages) {
        messages.forEach(msg -> {
            if ("FAIL".equals(msg)) throw new RuntimeException("Batch failure");
        });
    }

}
