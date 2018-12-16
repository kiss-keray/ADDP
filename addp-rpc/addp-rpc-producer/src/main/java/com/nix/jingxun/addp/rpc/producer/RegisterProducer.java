package com.nix.jingxun.addp.rpc.producer;

import com.nix.jingxun.addp.rpc.common.RPCInterfaceAnnotation;
import com.nix.jingxun.addp.rpc.producer.springboot.BootConfig;
import com.nix.jingxun.addp.rpc.producer.test.HelloImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.Annotation;

/**
 * @author keray
 * @date 2018/12/08 19:55
 */
@Configuration
@EnableConfigurationProperties(value = BootConfig.class)
public class RegisterProducer implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Object newBean = null;
        Class<?>[] interfaces = bean.getClass().getInterfaces();
        if (interfaces != null) {
            for (Class<?> clazz:interfaces) {
                Annotation[] annotations = clazz.getAnnotations();
                if (annotations != null && annotations.length > 0) {
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof RPCInterfaceAnnotation) {
                            String appName = ((RPCInterfaceAnnotation) annotation).appName();
                            String group = ((RPCInterfaceAnnotation) annotation).group();
                            String version = ((RPCInterfaceAnnotation) annotation).version();
                            try {
                                newBean = RPCProducer.registerProducer(clazz,bean, appName, group, version,newBean);
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new BeanCreationException(e.getMessage(),e.getCause());
                            }
                        }
                    }
                }
            }
        }
        return bean;
    }
}
