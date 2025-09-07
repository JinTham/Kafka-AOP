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

    @PostMapping
    public ResponseEntity<String> publish(@RequestBody MessageRequest msgRequest) {
        kafkaTemplate.send(msgRequest.topic(), msgRequest.message());
        return new ResponseEntity<>("Successfully sent " + msgRequest.message(), HttpStatus.OK);
    }

}
