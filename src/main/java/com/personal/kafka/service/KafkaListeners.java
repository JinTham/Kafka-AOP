package com.personal.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

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
    @KafkaListener(topics = "conversion-fail-topic", groupId = "jin-group")
    public void jsonListener(Integer dto) {
        // will fail if invalid argument type is sent
        System.out.println("Received DTO: " + dto);
    }

    // Acknowledgement/commit timeout fail
    @KafkaListener(topics = "ack-fail-topic", groupId = "jin-group")
    public void acknowledgementFail(@Payload String message, Acknowledgment ack) throws InterruptedException {
        log.info("Processing msg....");
        log.info("Processing complete, attempting ack...");
        ack.acknowledge();
        log.info("Ack done");
    }

}
