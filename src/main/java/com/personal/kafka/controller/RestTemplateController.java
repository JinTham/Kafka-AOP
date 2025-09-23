package com.personal.kafka.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping("api/v1/rest")
public class RestTemplateController {

    private static final String url = "https://jsonplaceholder.typicode.com/posts/sdf";

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping()
    public ResponseEntity<String> send() {
        log.info("Is RestTemplate proxied? {}", AopUtils.isAopProxy(restTemplate));
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return new ResponseEntity<>("Successfully sent: " + response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to send: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
