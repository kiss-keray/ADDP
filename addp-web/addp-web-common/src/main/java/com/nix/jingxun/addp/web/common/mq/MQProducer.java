package com.nix.jingxun.addp.web.common.mq;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.nix.jingxun.addp.web.common.config.MQConfig;
import com.nix.jingxun.addp.web.model.ReleaseBillModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

@Component
@Slf4j
public class MQProducer {
    private DefaultMQProducer producer;
    @Resource
    private MQConfig mqConfig;

    @PostConstruct
    public void init() {
        try {
            producer = new DefaultMQProducer(MQConfig.mqGroup);
            producer.setNamesrvAddr(MQConfig.rocketMQHost);
            producer.setInstanceName(MQConfig.mqInstanceName);
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
            log.error("mq  init error", e);
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void close() {
        producer.shutdown();
    }

    public void releaseData(Long billId, String content) {
        Message message = new Message(MQTopic.BILL_TP, MQTopic.BILL_RELEASE_TAG,
                JSON.toJSONBytes(
                        MapUtil.builder()
                                .put("billId", billId)
                                .put("data", content)
                                .build()
                ));
        try {
            SendResult result = producer.send(message);
            if (result.getSendStatus() != SendStatus.SEND_OK) {
                log.warn("消息通知异常{}", result);
            }
        }catch (Exception e) {
            log.error("mq send error:", e);
        }
    }


    public void billStatusChange(ReleaseBillModel billModel) {
        billStatusChange(billModel, 10);
    }

    public void billStatusChange(ReleaseBillModel billModel, int time) {
        if (time < 0) {
            log.error("mq send error:", new RuntimeException("mq send 重试失败"));
        }
        Message message = new Message(MQTopic.BILL_TP, MQTopic.BILL_STATUS_TAG, billModel.getId().toString().getBytes());
        try {
            log.info("bill start mq send {}", billModel.getId());
            SendResult result = producer.send(message);
            if (result.getSendStatus() != SendStatus.SEND_OK) {
                log.warn("消息通知异常{}", result);
            }
        } catch (InterruptedException | RemotingException e) {
            billStatusChange(billModel, --time);
        } catch (Exception e) {
            log.error("mq send error:", e);
        }
    }

}
