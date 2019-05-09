package com.nix.jingxun.addp.web.common.annotation;

import java.lang.annotation.*;

/**
 * @author Kiss
 * @date 2018/05/02 11:48
 * 标识controller为用户调用
 */
@Target( {ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MemberController {
}
