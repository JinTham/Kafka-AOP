package com.personal.kafka.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class KafkaAspect {

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

    @Pointcut("interceptCommonErrorHandler() || interceptKafkaListener()")
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
}

