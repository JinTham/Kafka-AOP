package com.personal.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String JIN_TOPIC = "jin-topic";

    @Bean
    public NewTopic topicConfig() {
        return TopicBuilder.name(JIN_TOPIC).build();
    }
}
