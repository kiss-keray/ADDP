package com.nix.jingxun.addp.web.common.annotation;

import java.lang.annotation.*;

/**
 * @author Kiss
 * @date 2018/05/01 22:06
 * 标识次controller或者controller方法不需要权限校验
 */
@Target( {ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Clear {
}
