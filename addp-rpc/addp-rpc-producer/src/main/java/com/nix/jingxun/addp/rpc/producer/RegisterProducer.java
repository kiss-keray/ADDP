package com.nix.jingxun.addp.rpc.producer;

import com.nix.jingxun.addp.rpc.common.RPCInterfaceAnnotation;
import com.nix.jingxun.addp.rpc.common.event.ProducerBeanPostEvent;
import com.nix.jingxun.addp.rpc.producer.springboot.BootConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.Resource;

/**
 * @author keray
 * @date 2018/12/08 19:55
 */
@EnableConfigurationProperties(value = {BootConfig.class})
public class RegisterProducer implements BeanPostProcessor {
    @Resource
    private ProducerEventProcessor producerEventProcessor;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?>[] interfaces = bean.getClass().getInterfaces();
        if (interfaces != null) {
            for (Class<?> clazz:interfaces) {
                if (clazz.getAnnotation(RPCInterfaceAnnotation.class) != null) {
                    producerEventProcessor.beanPostEvent(new ProducerBeanPostEvent(this,beanName,clazz));
                }
            }
        }
        return bean;
    }
}
