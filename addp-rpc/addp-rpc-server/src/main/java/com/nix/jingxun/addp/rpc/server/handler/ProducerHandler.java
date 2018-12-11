package com.nix.jingxun.addp.rpc.server.handler;

import com.alibaba.fastjson.JSON;
import com.alipay.remoting.util.RemotingUtil;
import com.alipay.remoting.util.StringUtils;
import com.nix.jingxun.addp.rpc.common.Producer2ServerRequest;
import com.nix.jingxun.addp.rpc.common.RPCMethodParser;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author keray
 * @date 2018/12/09 13:39
 */
@Component
@Slf4j
public class ProducerHandler {
    @Autowired
    private RedisTemplate<String, String> template;

    private final static int TIMEOUT_DEL_PRODUCER = 10000;
    private final static String RECON_DEL_PRODUCER_KEY = "RECON_DEL_PRODUCER_KEY";

    private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1, r -> {
        Thread thread = new Thread(r);
        thread.setName("rpc-server-producer-handler");
        return thread;
    });

    /**
     * 服务提供方注册服务
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean registerInterface(Producer2ServerRequest request, Channel channel) {
        log.info("服务注册 {}", request);
        template.opsForValue().set(RemotingUtil.parseRemoteAddress(channel), request.getHost());
        String interfaceKey = RPCMethodParser.getMethodKey(request.getInterfaceName(), request.getAppName(), request.getGroup(), request.getVersion());
        String producerHost = request.getHost();
        if (Boolean.valueOf(true).equals(template.opsForSet().isMember(interfaceKey,request.getHost())) || Long.valueOf(1).equals(template.opsForSet().add(interfaceKey, request.getHost()))) {
            template.opsForSet().add(producerHost, JSON.toJSONString(request));
            log.info("服务注册success");
            return true;
        }
        log.info("服务注册fail");
        return false;
    }

    public boolean reconnect(Channel channel) {
        return Long.valueOf(1).equals(template.opsForSet().remove(RECON_DEL_PRODUCER_KEY,RemotingUtil.parseRemoteAddress(channel)));
    }

    /**
     * 消费者获取接口信息
     */
    public String consumerGetInterfaceMsg(String interfaceKey) {
        log.info("获取服务 {}", interfaceKey);
        return template.opsForSet().randomMember(interfaceKey);
    }

    public void producerLeave(Channel channel) {
        String chanelUrl = RemotingUtil.parseRemoteAddress(channel);
        template.opsForSet().add(RECON_DEL_PRODUCER_KEY,chanelUrl);
        scheduledExecutorService.schedule(() -> {
            if (Boolean.valueOf(true).equals(template.opsForSet().isMember(RECON_DEL_PRODUCER_KEY,chanelUrl))) {
                template.delete(chanelUrl);
                String host = template.opsForValue().get(chanelUrl);
                if (StringUtils.isNotBlank(host)) {
                    template.delete(chanelUrl);
                    String interfaceMsgJson = template.opsForValue().get(host);
                    template.delete(host);
                    if (StringUtils.isNotBlank(interfaceMsgJson)) {
                        Producer2ServerRequest interfaceMsg = JSON.parseObject(interfaceMsgJson, Producer2ServerRequest.class);
                        String interfaceKey = RPCMethodParser.getMethodKey(interfaceMsg.getInterfaceName(), interfaceMsg.getAppName(), interfaceMsg.getGroup(), interfaceMsg.getVersion());
                        template.opsForSet().remove(interfaceKey, host);
                        if (Long.valueOf(0).equals(template.opsForSet().size(interfaceKey))) {
                            template.delete(interfaceKey);
                        }
                    }
                }
            }
        },TIMEOUT_DEL_PRODUCER, TimeUnit.MILLISECONDS);

    }
}
