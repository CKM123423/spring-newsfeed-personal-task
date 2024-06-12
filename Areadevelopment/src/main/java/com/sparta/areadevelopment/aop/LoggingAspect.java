package com.sparta.areadevelopment.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@Slf4j(topic = "LoggingAop")
@Aspect
public class LoggingAspect {

    @Pointcut("execution(* com.sparta.areadevelopment.controller..*(..))")
    private void controller() {
    }

    @Before("controller()")
    public void logBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        log.info("Request URL: {}", request.getRequestURL().toString());
        log.info("HTTP Method: {}", request.getMethod());
    }

}
