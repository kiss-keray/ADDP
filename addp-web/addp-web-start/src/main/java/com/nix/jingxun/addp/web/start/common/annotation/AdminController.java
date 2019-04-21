package com.nix.jingxun.addp.web.start.common.annotation;

import java.lang.annotation.*;

/**
 * @author Kiss
 * @date 2018/05/02 11:47
 * 标识controller为管理调用
 * 方便权限管理拦截
 */
@Target( {ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdminController {
}
