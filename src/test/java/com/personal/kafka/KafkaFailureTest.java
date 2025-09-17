package com.personal.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "existing-topic", brokerProperties = {"auto.create.topics.enable=false"})
@EnableAspectJAutoProxy
@DirtiesContext
class KafkaFailureTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void testTopicNotFound() throws Exception {
        assertThrows(KafkaException.class, () -> {
            // Send to a non-existing topic
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("non-existing-topic", "test-message");
//            future.whenComplete((res, ex) -> {
//                if (ex != null) {
//                    log.info("xxxxxxx");
//                    assertThat(ex.getCause()).isInstanceOf(UnknownTopicOrPartitionException.class);
//                }
//            });
            assertThrows(Exception.class, () -> future.get());
        });
    }


}