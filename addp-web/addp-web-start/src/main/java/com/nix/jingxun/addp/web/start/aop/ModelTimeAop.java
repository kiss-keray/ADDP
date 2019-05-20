package com.nix.jingxun.addp.web.start.aop;

import com.nix.jingxun.addp.web.model.BaseModel;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author keray
 * @date 2019/05/20 16:41
 */

@Aspect
@Component
public class ModelTimeAop {

    @Pointcut("execution(* com.nix.jingxun.addp.web.service.base.BaseServiceImpl.update(*))")
    public void update() {}

    @Before("update()")
    public void updateBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        BaseModel model = (BaseModel) args[0];
        model.setModifyTime(LocalDateTime.now());
    }

    @Pointcut("execution(* com.nix.jingxun.addp.web.service.base.BaseServiceImpl.save(*))")
    public void save() {}

    @Before("save()")
    public void saveBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        BaseModel model = (BaseModel) args[0];
        model.setCreateTime(LocalDateTime.now());
        model.setModifyTime(LocalDateTime.now());
    }
}
