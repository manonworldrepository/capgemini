package com.capgemini.twilight.assessment.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut("within(com.capgemini.twilight.assessment..*controller..*)")
    public void controllerPointcut() {
    }

    @Pointcut("within(com.capgemini.twilight.assessment..*service..*)")
    public void servicePointcut() {
    }

    @Pointcut("within(com.capgemini.twilight.assessment.exception.GlobalExceptionHandler)")
    public void exceptionHandlerPointcut() {
    }

    @AfterThrowing(pointcut = "controllerPointcut() || servicePointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("EXCEPTION in {}.{}() with cause = '{}' and exception = '{}'",
            joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName(),
            e.getCause() != null ? e.getCause() : "NULL",
            e.getMessage()
        );
    }

    @Around("controllerPointcut() || servicePointcut() || exceptionHandlerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isInfoEnabled()) {
            log.info("==> Enter: {}.{}() with argument[s] = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs())
            );
        }

        try {
            long startTime = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            if (log.isInfoEnabled()) {
                log.info("<== Exit: {}.{}() with result = {}. Execution time = {} ms",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    result,
                    endTime - startTime
                );
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()",
                Arrays.toString(joinPoint.getArgs()),
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName()
            );
            throw e;
        }
    }
}