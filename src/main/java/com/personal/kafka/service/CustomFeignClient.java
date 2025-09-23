package com.personal.kafka.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "postClient", url = "https://jsonplaceholder.typicode.com")
public interface CustomFeignClient {

    @GetMapping("/posts/{id}/sdf")
    String getPostById(@PathVariable("id") Long id);
}
