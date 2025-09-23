package com.personal.kafka.config.restTemplate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Slf4j
@Component
public class CustomResponseErrorHandler implements ResponseErrorHandler {

    private final ResponseErrorHandler delegate = new DefaultResponseErrorHandler();

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return delegate.hasError(response);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
//        delegate.handleError(response); // delegate back
        log.info("swallow");
    }
}
