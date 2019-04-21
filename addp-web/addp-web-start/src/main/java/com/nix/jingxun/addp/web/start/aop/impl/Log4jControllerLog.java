package com.nix.jingxun.addp.web.start.aop.impl;

import com.nix.jingxun.addp.web.start.aop.ControllerLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

/**
 * @author 11723
 */
@Component
public class Log4jControllerLog implements ControllerLog {
    private final static Log log = LogFactory.getLog("nix-");

    @Override
    public void before(JoinPoint joinPoint) {
        log.info("===============start method===================");
        if (joinPoint != null) {
            log.info(joinPoint.getSignature().getDeclaringTypeName());
            log.info(joinPoint.getSignature().getName());
            log.info(joinPoint.getArgs());
        }
        log.info("==============================================");
    }

    @Override
    public void after(Object returnObject) {
        log.info("+++++++++++++++++method return+++++++++++++++++");
        if (returnObject != null) {
            log.info("return:" + (returnObject.toString().length() > 1024 ? returnObject.hashCode() : returnObject));
        }
        log.info("+++++++++++++++++++++++++++++++++++++++++++++++");
    }
}
