package com.personal.kafka.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RestTemplateAspect {

    /**
     * RestTemplate by default will have a DefaultResponseErrorHandler.
     * Once the Client response, RestTemplate will handleResponse() which call hasError().
     * If hasError() is true, it will invoke ResponseErrorHandler handleError().
     * The handleError() method will subsequently propagate the exception up to RestTemplate methods.
     * This DefaultResponseErrorHandler can be overridden by user (needs to override with ResponseErrorHandler.Class)
     *
     * If users implement their own ResponseErrorHandler where the implementation swallows the exception,
     * point-cutting at RestTemplate methods (After-throwing/Around) won't be able to intercept it.
     *
     * One RestTemplate can only be configured with one ResponseErrorHandler.
     */

    // Pointcut expression for RestTemplate methods
    @Pointcut("execution(public * org.springframework.web.client.RestTemplate.*(..))")
    private void interceptRestTemplateMethods() {}

    @Around("interceptRestTemplateMethods()")
    public void interceptAroundRestTemplate(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("Intercepted around RestTemplate methods.");
        try {
            proceedingJoinPoint.proceed();
            log.info("No error");
        } catch (Exception ex) {
            log.error("error");
            throw ex;
        }
    }

    @Before("interceptRestTemplateMethods()")
    public void interceptBeforeRestTemplate(JoinPoint joinPoint) {
        log.info("Intercepted before calling RestTemplate methods.");
    }

    @AfterThrowing(pointcut = "interceptRestTemplateMethods()", throwing = "ex")
    public void interceptRestTemplateAfterThrowing(Throwable ex) throws Throwable {
        log.error("Intercepted after RestTemplate methods throw error: {}", ex.getMessage(), ex);
        throw ex;
    }


    /**
     * Pointcut expression for RestTemplate error handler (NOT WORKING)
     * RestTemplate calls handleError() directly on that object, not through a Spring proxy.
     * Even if MyErrorHandler is a Spring bean, the instance used in RestTemplate is the new instance, not the proxied bean.
     */
    @Before("execution(* org.springframework.web.client.ResponseErrorHandler.*(..))")
    public void logBeforeHandleError(JoinPoint joinPoint) {
        log.info("➡️ RestTemplate error being handled by: {}", joinPoint.getTarget().getClass().getSimpleName());
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            log.info("   Arg: {}", arg);
        }
    }

}

