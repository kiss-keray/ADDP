package com.nix.jingxun.addp.rpc.producer;

import com.nix.jingxun.addp.rpc.common.event.ProducerBeanPostEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

/**
 * create by keray
 * date:2019/7/16 10:30
 */
public interface ProducerEventProcessor {

    @EventListener(value = ProducerBeanPostEvent.class)
    void beanPostEvent(ProducerBeanPostEvent producerBeanPostEvent);

    @EventListener
    void contextStartEvent(ApplicationStartedEvent startedEvent);
}
