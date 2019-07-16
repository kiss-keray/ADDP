package com.nix.jingxun.addp.rpc.common.event;

import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * create by keray
 * date:2019/7/16 10:19
 * 服务接口注入完成后出发事件
 */
@ToString
public class ProducerBeanPostEvent extends ApplicationEvent {
    private final String beanName;
    private final Class clazz;
    public ProducerBeanPostEvent(Object source,String beanName,Class clazz) {
        super(source);
        if (beanName == null || clazz == null) {
            throw new IllegalArgumentException("beanName or clazz is null");
        }
        this.beanName = beanName;
        this.clazz = clazz;
    }

    public String getBeanName() {
        return beanName;
    }
    public Class getClazz() {
        return clazz;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProducerBeanPostEvent) {
            ProducerBeanPostEvent event = (ProducerBeanPostEvent) obj;
            return (beanName + clazz.getName()).equals(event.getBeanName() + event.getClazz().getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return clazz.hashCode();
    }
}
