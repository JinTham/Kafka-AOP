package com.personal.kafka.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Aspect
@Component
public class KafkaAspect {

    /**
     * Intercept exceptions thrown from any kafkaTemplate method
     * Only can catch Sync, Async will be caught by ProducerInterceptor instead)
     */
    @Pointcut("execution(public * org.springframework.kafka.core.KafkaTemplate.send*(..))")
    public void interceptKafkaTemplate() { };

    /**
     * Intercept exceptions thrown from any @KafkaListener method
     */
    @Pointcut("@annotation(org.springframework.kafka.annotation.KafkaListener)")
    public void interceptKafkaListener() { };

    /**
     * Intercept before any method in CommonErrorHandler interface
     * or any subclass/implementation of it.
     */
    @Pointcut("execution(* org.springframework.kafka.listener.CommonErrorHandler+.*(..))")
    public void interceptCommonErrorHandler() { };

    @Pointcut("interceptCommonErrorHandler() || interceptKafkaListener() || interceptKafkaTemplate()")
    public void interceptKafka() {};

    @AfterThrowing(pointcut = "interceptKafka()", throwing = "ex")
    public void handleKafkaListenerException(Exception ex) {
        log.error("Kafka Listener Exception: " + ex.getClass().getName() + " - " + ex.getMessage());
    }

    @Before("interceptCommonErrorHandler()")
    public void handleCommonErrorHandler(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Exception) {
            Exception ex = (Exception) args[0];
            log.error("Exception within ErrorHandler: " + ex.getMessage());
        }
    }

    @Around("interceptKafkaTemplate()")
    public Object interceptKafkaTemplateSend(ProceedingJoinPoint pjp) throws Throwable {
        // Call the original KafkaTemplate.send()
        Object result = pjp.proceed();

        if (result instanceof CompletableFuture<?> future && future.get() instanceof SendResult<?,?>) {
            // Attach callback to CompletableFuture
            future.whenComplete((res, ex) -> {
                log.info("Intercepted CompletableFuture");
                if (ex != null) {
                    log.error("Kafka send failed: {}", ex.getMessage());
                }
            });
            // Return the future back to caller
            return future;
        }
        return result;
    }
}

