package com.nix.jingxun.addp.rpc.common.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.stereotype.Component;

/**
 * @author keray
 * @date 2018/12/07 17:46
 */
public class SpringAnnotationHandlerSupport extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("springAnnotation", new SpringAnnotationParser());
    }
}
