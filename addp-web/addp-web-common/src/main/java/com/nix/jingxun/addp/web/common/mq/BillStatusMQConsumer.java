package com.nix.jingxun.addp.web.common.mq;

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
public class BillStatusMQConsumer {
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
            billStatusConsumer.subscribe(MQTopic.BILL_STATUS_TP, "*");
            billStatusConsumer.registerMessageListener((MessageListenerConcurrently) (messages, context) -> {
                for (MessageExt msg : messages) {
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
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });
            billStatusConsumer.start();
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void close() {
        billStatusConsumer.shutdown();
    }
}
