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

    @AfterThrowing(pointcut = "interceptKafkaTemplate()", throwing = "ex")
    public void handleKafkaTemplateException(Exception ex) throws Throwable {
        log.error("Kafka Template Exception: " + ex.getClass().getName() + " - " + ex.getMessage());
        throw ex;
    }

    @Before("interceptCommonErrorHandler()")
    public void handleCommonErrorHandler(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Exception ex) {
            log.error("Exception within ErrorHandler: " + ex.getMessage());
        }
    }

//    @Around("interceptKafkaTemplate()")
//    public Object interceptKafkaTemplateSend(ProceedingJoinPoint pjp) throws Throwable {
//        // Call the original KafkaTemplate.send()
//        Object result = pjp.proceed();
//
//        if (result instanceof CompletableFuture<?> future && future.get() instanceof SendResult<?,?>) {
//            // Attach callback to CompletableFuture
//            future.whenComplete((res, ex) -> {
//                log.info("Intercepted CompletableFuture");
//                if (ex != null) {
//                    log.error("Kafka send failed: {}", ex.getMessage());
//                }
//            });
//            // Return the future back to caller
//            return future;
//        }
//        return result;
//    }
}

