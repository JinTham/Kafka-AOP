package com.personal.kafka.controller;

import com.personal.kafka.dto.MessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/kafka")
public class KafkaController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, Integer> kafkaTemplateInteger;

    @PostMapping()
    public ResponseEntity<String> publish(@RequestBody MessageRequest msgRequest) {
        kafkaTemplate.send(msgRequest.topic(), msgRequest.message());
        return new ResponseEntity<>("Successfully sent " + msgRequest.message(), HttpStatus.OK);
    }

    /** Intercepted by "interceptKafkaTemplate()"
     * org.springframework.kafka.KafkaException: Send failed
     * org.apache.kafka.common.errors.TimeoutException: Topic non-existing-topic not present in metadata after 60000 ms.
     */
    @PostMapping(value = "/wrong-topic")
    public ResponseEntity<String> publishWrongTopic(@RequestBody MessageRequest msgRequest) {
        kafkaTemplate.send("non-existing-topic", msgRequest.message());
        return new ResponseEntity<>("Failed to send " + msgRequest.message(), HttpStatus.OK);
    }

    /**
     * Intercepted by "interceptKafkaTemplate()"
     * org.apache.kafka.common.errors.SerializationException:
     * Can't convert value of class java.lang.Integer to class org.apache.kafka.common.serialization.StringSerializer specified in value.serializer
     */
    @PostMapping(value = "/wrong-serializer")
    public ResponseEntity<String> publishWrongType(@RequestBody MessageRequest msgRequest) {
        kafkaTemplateInteger.send(msgRequest.topic(), 123);
        return new ResponseEntity<>("Failed to send " + msgRequest.message(), HttpStatus.OK);
    }

}
