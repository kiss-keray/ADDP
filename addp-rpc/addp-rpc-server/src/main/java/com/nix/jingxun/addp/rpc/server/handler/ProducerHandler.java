package com.nix.jingxun.addp.rpc.server.handler;

import com.alibaba.fastjson.JSON;
import com.nix.jingxun.addp.rpc.common.Producer2ServerRequest;
import com.nix.jingxun.addp.rpc.common.RPCMethodParser;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @author keray
 * @date 2018/12/09 13:39
 */
@Component
public class ProducerHandler {
    @Autowired
    private StringRedisTemplate template;

    /**
     * 服务提供方注册服务
     * */
    @Transactional(rollbackFor = Exception.class)
    public boolean registerInterface(Producer2ServerRequest request) {
        String interfaceKey = RPCMethodParser.getMethodKey(request.getInterfaceName(),request.getAppName(),request.getGroup(),request.getVersion());
        String producerHost = request.getHost();
        if (template.opsForZSet().add(interfaceKey,producerHost,1.0).booleanValue()) {
            template.opsForValue().set(producerHost, JSON.toJSONString(request));
            return true;
        }
        return false;
    }

    /**
     * 消费者获取接口信息
     * */
    public String consumerGetInterfaceMsg(String interfaceKey) {
        return template.opsForValue().get(interfaceKey);
    }
}
