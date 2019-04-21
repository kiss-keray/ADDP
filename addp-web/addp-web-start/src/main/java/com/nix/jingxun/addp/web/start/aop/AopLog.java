package com.nix.jingxun.addp.web.start.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Aspect
@Component
class AopLog {
    @Resource
    private ControllerLog log;
    @Pointcut("execution(* com.nix.jingxun.addp.web.start.controller..*(..))")
    public void controllerMethod() {}

    @Before("controllerMethod()")
    public void before(JoinPoint joinPoint) {
        log.before(joinPoint);
    }
    @AfterReturning(returning = "object",pointcut = "controllerMethod()")
    public void after(Object object) {
        log.after(object);
    }
}
