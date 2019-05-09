package com.nix.jingxun.addp.web.common.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 11723
 * controller异常处理类
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    /**
     * 对于controller方法执行出现
     * {@link IllegalArgumentException}
     *Assert校验异常
     * 异常进行处理
     * */
    @ResponseBody
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Object exceptionHandle(IllegalArgumentException e) {
        e.printStackTrace();
        return null;
    }
    /**
     * 对于controller方法执行出现
     * {@link Exception}
     * 前面没处理的异常
     * 异常进行处理
     * */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Object exceptionHandle(Exception e) {
        e.printStackTrace();
        return null;
    }
}
