package com.personal.kafka.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
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
    public void handleKafkaTemplateException(JoinPoint joinPoint, Exception ex) throws Throwable {
        log.error("Kafka Template Exception");
        logKafkaErrors(joinPoint, ex);
        throw ex;
    }

    @Before("interceptCommonErrorHandler()")
    public void handleCommonErrorHandler(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Exception ex) {
            log.error("Exception within ErrorHandler");
            logKafkaErrors(joinPoint, ex);
        }
    }

    private void logKafkaErrors(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.error("Executing Kafka operation: {} with arguments: {}", methodName, args);

        try {
            if (ex instanceof ListenerExecutionFailedException e) {
                // Handle exceptions thrown by the message listener itself (consumer side)
                logError("Kafka Listener execution failed in method", methodName, e);
            } else if (ex instanceof org.apache.kafka.common.errors.SerializationException e) {
                // Handle serialization issues
                logError("Kafka Serialization error in method", methodName, e);
            } else if (ex instanceof org.apache.kafka.common.errors.TimeoutException e) {
                // Handle Kafka client timeouts (e.g. send timeout)
                logError("Kafka Timeout error in method", methodName, e);
            } else if (ex instanceof KafkaException e) {
                // Handle other Spring Kafka exceptions
                logError("Spring Kafka error in method", methodName, e);
            } else if (ex instanceof RuntimeException e) {
                // Catch any unexpected runtime exceptions that might occur during Kafka operations
                logError("Unexpected Runtime error during Kafka operation", methodName, e);
            } else {
                log.error("Unexpected error in Kafka operation {}: {}", methodName, ex.getMessage());
            }
        } catch (Exception e) {
            log.error("Exception in Kafka Error Logging aspect: {}", e.getMessage());
        }
    }

    private void logError(String msg, String methodName, Exception ex) {
        log.error("{} {}: {}; {}", msg, methodName, ex.getMessage(), ex.getCause() != null ? ex.getCause().getMessage() : "");
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

