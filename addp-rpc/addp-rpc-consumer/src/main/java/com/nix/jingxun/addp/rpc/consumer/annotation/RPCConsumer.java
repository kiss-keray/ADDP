package com.nix.jingxun.addp.rpc.consumer.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author keray
 * @date 2018/12/07 15:34
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RPCConsumer {
    String appName();
    String group() default "RPC";
    String version() default "1.0.0";
}
