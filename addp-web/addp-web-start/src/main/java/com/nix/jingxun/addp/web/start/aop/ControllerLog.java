package com.nix.jingxun.addp.web.start.aop;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

/**
 * @author 11723
 * controller执行方法记录接口
 */
@Component
public interface ControllerLog {
    /**
     * controller方法调用前执行
     * @param joinPoint 调用controller方法的参数
     * */
    void before(JoinPoint joinPoint);
    /**
     * controller方法调用结束执行
     * @param returnObject 方法返回的参数
     * */
    void after(Object returnObject);
}
