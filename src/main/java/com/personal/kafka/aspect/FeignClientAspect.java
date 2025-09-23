package com.personal.kafka.aspect;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class FeignClientAspect {

    /**
     * FeignClient by default has a ErrorDecoder.Default for handling error, unless being overridden.
     * If response statusCode not 2xx, Feign-Core ResponseHandler will incoke ErrorDecoder.decode()
     * ErrorDecoder.decode() must return an Exception, therefore user can't have a custom implementation that swallows the exception.
     * The exception generated from ErrorDecoder.decode() will propagate all the way up to @FeignClient methods.
     * Hence, pointcut at @FeignClient methods is sufficient.
     *
     * One FeignClient can only be configured with one ErrorDecode.
     */

    // Intercept any public method in a Feign client
    @AfterThrowing(pointcut = "within(@org.springframework.cloud.openfeign.FeignClient *)", throwing = "ex")
    public void handleFeignException(JoinPoint joinPoint, Throwable ex) {

        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.error("Executing FeignClient operation: {} with arguments: {}", methodName, args);

        try {
            if (ex instanceof FeignException.FeignClientException e) {
                // Handle Client issues
                logError("FeignClient client side failed in method", methodName, e);
            } else if (ex instanceof FeignException.FeignServerException e) {
                // Handle Server issues
                logError("FeignClient server side failed in method", methodName, e);
            } else if (ex instanceof RuntimeException e) {
                // Catch any unexpected runtime exceptions that might occur during FeignClient operations
                logError("Unexpected Runtime error during FeignClient operation", methodName, e);
            } else {
                log.error("Unexpected error in FeignClient operation {}: {}", methodName, ex.getMessage());
            }
        } catch (Exception e) {
            log.error("Exception in FeignClient Error Logging aspect: {}", e.getMessage());
        }
    }

    private void logError(String msg, String methodName, Exception ex) {
        log.error("{} {}: {}; {}", msg, methodName, ex.getMessage(), ex.getCause() != null ? ex.getCause().getMessage() : "");
    }

}
