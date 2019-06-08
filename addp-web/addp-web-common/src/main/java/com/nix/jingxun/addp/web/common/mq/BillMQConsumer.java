package com.nix.jingxun.addp.web.common.mq;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nix.jingxun.addp.web.common.config.MQConfig;
import com.nix.jingxun.addp.web.iservice.IReleaseBillService;
import com.nix.jingxun.addp.web.iservice.IWebSocket;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Arrays;

@Component
@Slf4j
public class BillMQConsumer {
    @Resource
    private IWebSocket webSocket;
    @Resource
    private MQConfig mqConfig;
    @Resource
    private IReleaseBillService releaseBillService;
    private DefaultMQPushConsumer billStatusConsumer;

    @PostConstruct
    public void billStatusConsumer() {
        initBillStatusConsumer();
    }

    public void initBillStatusConsumer() {
        billStatusConsumer = new DefaultMQPushConsumer(MQConfig.mqGroup);
        billStatusConsumer.setNamesrvAddr(MQConfig.rocketMQHost);
        billStatusConsumer.setInstanceName(MQConfig.mqInstanceName);
        billStatusConsumer.setMessageModel(MessageModel.BROADCASTING);
        try {
            billStatusConsumer.subscribe(MQTopic.BILL_TP, StrUtil.format("{} || {}",MQTopic.BILL_STATUS_TAG,MQTopic.BILL_RELEASE_TAG));
            billStatusConsumer.registerMessageListener((MessageListenerConcurrently) (messages, context) -> {
                for (MessageExt msg : messages) {
                    switch (msg.getTags()) {
                        case MQTopic.BILL_STATUS_TAG: notifyBillStatus(msg);break;
                        case MQTopic.BILL_RELEASE_TAG: sendReleaseData(msg);break;
                        default:break;
                    }
                }
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            });
            billStatusConsumer.start();
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        }
    }

    private void notifyBillStatus(MessageExt msg) {
        try {
            Long billId = Long.valueOf(new String(msg.getBody()));
            webSocket.notifyClient(releaseBillService.findById(billId));
        } catch (NumberFormatException e) {
            log.error("接收到billId解析错误 {}", Arrays.toString(msg.getBody()));
        } catch (Exception e) {
            // 实时性异常也直接成功
            log.error("通知客户端bill status error", e);
        }
    }

    private void sendReleaseData(MessageExt msg) {
        try {
            JSONObject json = JSON.parseObject(new String(msg.getBody()));
            webSocket.pushReleaseData(json.getLong("billId"),json.getString("data"));
        }catch (Exception e) {
            log.error("发送发布内容失败 :{}",msg);
        }
    }

    @PreDestroy
    public void close() {
        billStatusConsumer.shutdown();
    }
}
