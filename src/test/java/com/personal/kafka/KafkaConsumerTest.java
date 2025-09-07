package com.personal.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"test-topic"}, brokerProperties = {"listeners=PLAINTEXT://localhost:9092"})
public class KafkaConsumerTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaListenerEndpointRegistry registry; // to get listener containers

    @Test
    public void simulateWakeupException() throws InterruptedException {
        // Send a message
        kafkaTemplate.send("jin-topic", "msg1");

        // Give container time to start and process
        Thread.sleep(1000);

        // Stop the listener container to trigger WakeupException internally
        registry.getListenerContainer("testListeners.listener").stop(); // beanId.methodName

        System.out.println("Listener container stopped, WakeupException should occur inside Spring container");

        Thread.sleep(2000); // wait for any internal handling
    }
}
