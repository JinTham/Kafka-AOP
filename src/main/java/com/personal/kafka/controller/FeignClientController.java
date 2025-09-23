package com.personal.kafka.controller;

import com.personal.kafka.service.CustomFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/feign")
public class FeignClientController {

    @Autowired
    private CustomFeignClient feignClient;

    @PostMapping()
    public ResponseEntity<String> callFeign() {
        log.info("Is RestTemplate proxied? {}", AopUtils.isAopProxy(feignClient));
        try {
            String response = feignClient.getPostById(12345L);
            return new ResponseEntity<>("Successfully sent: " + response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to send: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
