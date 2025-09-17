package com.personal.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaListeners {

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplateObject;

    // Successful
    @KafkaListener(topics = "good-topic", groupId = "jin-group")
    void listener(String data) {
        log.info("Listener received: {}", data);
    }

    /**
     * Business Logic Error
     * Intercepted by "interceptCommonErrorHandler()" or "interceptKafkaListener()"
     * org.springframework.kafka.listener.ListenerExecutionFailedException:
     * Listener method 'void com.personal.kafka.service.KafkaListeners.listenerLogicFail(java.lang.String) throws java.lang.Exception' threw exception
     */
    @KafkaListener(topics = "business-fail-topic", groupId = "jin-group")
    void listenerLogicFail(String data) throws Exception {
        log.error("Service throw exception");
        throw new Exception("Service throw exception");
    }

    /**
     * Deserialization Error (wrong dto type)
     * Intercepted by "interceptCommonErrorHandler()"
     * org.springframework.kafka.listener.ListenerExecutionFailedException: Listener method could not be invoked with the incoming message
     * org.springframework.messaging.converter.MessageConversionException: Cannot handle message
     */
    @KafkaListener(topics = "deserialize-fail-topic", groupId = "jin-group")
    public void listenerDeserializeFail(Integer dto) {
        log.info("Received DTO: " + dto);
    }

    /** Intercepted by "interceptKafkaTemplate()"
     * org.springframework.kafka.KafkaException: Send failed
     * org.apache.kafka.common.errors.TimeoutException: Topic output-topic not present in metadata after 60000 ms.
     */
    @KafkaListener(topics = "redirect-fail-topic")
    @SendTo("output-topic")
    public String listenerRedirectToNonExistingTopic(String data) {
        // @SendTo internally looks for a KafkaTemplate<Object,Object> to send out the result
        return data.toUpperCase();
    }

}
