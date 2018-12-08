package com.nix.jingxun.addp.rpc.producer;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

/**
 * @author keray
 * @date 2018/12/08 19:55
 */
@Component
public class ProxyConsumer implements BeanPostProcessor {

    @Autowired
    private DefaultListableBeanFactory defaultListableBeanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Annotation[] annotations = bean.getClass().getAnnotations();
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation:annotations) {
//                if (annotation instanceof RPCConsumer) {
//                    return Proxy.newProxyInstance(handler.getClass().getClassLoader(), bean.getClass().getInterfaces(), handler);
//                }
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
