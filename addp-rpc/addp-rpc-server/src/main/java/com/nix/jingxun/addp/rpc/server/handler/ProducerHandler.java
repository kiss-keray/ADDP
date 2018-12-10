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
import java.util.stream.Collectors;

/**
 * @author keray
 * @date 2018/12/09 13:39
 */
@Component
@Slf4j
public class ProducerHandler {
    @Autowired
    private RedisTemplate<String,String> template;

    /**
     * 服务提供方注册服务
     * */
    @Transactional(rollbackFor = Exception.class)
    public boolean registerInterface(Producer2ServerRequest request,Channel channel) {
        log.info("服务注册 {}",request);
        template.opsForValue().set(RemotingUtil.parseRemoteAddress(channel),request.getHost());
        String interfaceKey = RPCMethodParser.getMethodKey(request.getInterfaceName(), request.getAppName(), request.getGroup(), request.getVersion());
        String producerHost = request.getHost();
        if (Long.valueOf(1).equals(template.opsForSet().add(interfaceKey,request.getHost()))) {
            template.opsForValue().set(producerHost, JSON.toJSONString(request));
            return true;
        }
        return false;
    }

    /**
     * 消费者获取接口信息
     * */
    public String consumerGetInterfaceMsg(String interfaceKey) {
        log.info("获取服务 {}",interfaceKey);
        return template.opsForSet().randomMember(interfaceKey);
    }

    public void producerLeave(Channel channel) {
        String host = template.opsForValue().get(RemotingUtil.parseRemoteAddress(channel));
        template.delete(RemotingUtil.parseRemoteAddress(channel));
        if (StringUtils.isNotBlank(host)) {
            String interfaceMsgJson = template.opsForValue().get(host);
            template.delete(host);
            if (StringUtils.isNotBlank(interfaceMsgJson)) {
                Producer2ServerRequest interfaceMsg = JSON.parseObject(interfaceMsgJson,Producer2ServerRequest.class);
                String interfaceKey = RPCMethodParser.getMethodKey(interfaceMsg.getInterfaceName(), interfaceMsg.getAppName(), interfaceMsg.getGroup(), interfaceMsg.getVersion());
                template.opsForSet().remove(interfaceKey, host);
                if (Long.valueOf(0).equals(template.opsForSet().size(interfaceKey))) {
                    template.delete(interfaceKey);
                }
            }
        }
    }
}
