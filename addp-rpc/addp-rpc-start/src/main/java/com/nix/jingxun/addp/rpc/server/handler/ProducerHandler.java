package com.nix.jingxun.addp.rpc.server.handler;

import com.alibaba.fastjson.JSON;
import com.alipay.remoting.util.RemotingUtil;
import com.alipay.remoting.util.StringUtils;
import com.nix.jingxun.addp.rpc.common.Producer2ServerRequest;
import com.nix.jingxun.addp.rpc.common.RPCMethodParser;
import com.nix.jingxun.addp.rpc.common.config.CommonConfig;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
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
    private final static String PRODUCER_KEY = "PRODUCER_KEY";
    private final static String INTERFACE_DETAIL_SUFFIX = "-detail";

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
        // 服务连接url到服务host映射 用户断开移除服务 (s) C -> A
        template.opsForValue().set(RemotingUtil.parseRemoteAddress(channel), request.getHost());
        // 服务签名到服务提供方映射 (s) B1 -> A(s)
        String interfaceKey = new RPCMethodParser.ServiceModel(request.getInterfaceName(), request.getAppName(), request.getGroup(), request.getVersion()).getKey();
        String producerHost = request.getHost();
        if (Boolean.valueOf(true).equals(template.opsForSet().isMember(interfaceKey,producerHost)) || Long.valueOf(1).equals(template.opsForSet().add(interfaceKey, producerHost))) {
            //服务提供方到提供所有服务的映射 (s) A -> B1(s)
            template.opsForSet().add(producerHost, interfaceKey);
            // 服务接口详情映射 (s) B1 -> B2
            if (Boolean.FALSE.equals(template.hasKey(interfaceKey + INTERFACE_DETAIL_SUFFIX))) {
                template.opsForValue().set(interfaceKey + INTERFACE_DETAIL_SUFFIX, JSON.toJSONString(request));
            }
            // 所有服务集合 producer_key -> B1(s)
            template.opsForSet().add(PRODUCER_KEY,interfaceKey);
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

    /**
     * 一个服务提供方断开连接
     * */
    public void producerLeave(Channel channel) {
        String chanelUrl = RemotingUtil.parseRemoteAddress(channel);
        template.opsForSet().add(RECON_DEL_PRODUCER_KEY,chanelUrl);
        scheduledExecutorService.schedule(() -> {
            if (Boolean.valueOf(true).equals(template.opsForSet().isMember(RECON_DEL_PRODUCER_KEY,chanelUrl))) {
                template.opsForSet().remove(RECON_DEL_PRODUCER_KEY,chanelUrl);
                log.info("移除服务方 {}",chanelUrl);
                String host = template.opsForValue().get(chanelUrl);
                if (StringUtils.isNotBlank(host)) {
                    template.delete(chanelUrl);
                    Set<String> interfaceKeys = template.opsForSet().members(host);
                    template.delete(host);
                    if (interfaceKeys != null) {
                        interfaceKeys.forEach(interfaceKey -> {
                            if (StringUtils.isNotBlank(interfaceKey)) {
                                template.opsForSet().remove(interfaceKey, host);
                                // 如果服务提供方全部断开连接
                                if (Boolean.FALSE.equals(template.hasKey(interfaceKey))) {
                                    template.delete(interfaceKey + INTERFACE_DETAIL_SUFFIX);
                                    template.opsForSet().remove(PRODUCER_KEY,interfaceKey);
                                }
                            }
                        });
                    }
                }
            }
        },TIMEOUT_DEL_PRODUCER, TimeUnit.MILLISECONDS);

    }

    /**
     *  根据服务名查询服务
     * */
    public List<RPCMethodParser.ServiceModel> serviceSearch(String key) {
        Set<String> stringSet = template.opsForSet().members(PRODUCER_KEY);
        return stringSet == null ? null : stringSet.parallelStream().map(RPCMethodParser::methodKey2Model).filter(item -> match(key,item)).collect(Collectors.toList());
    }
    /**
     * 根据应用名查询服务
     * */
    public List<RPCMethodParser.ServiceModel> appSearch(String app) {
        Set<String> stringSet = template.opsForSet().members(PRODUCER_KEY);
        return stringSet == null ? null : stringSet.parallelStream().map(RPCMethodParser::methodKey2Model).filter(item -> app.equalsIgnoreCase(item.getAppName())).collect(Collectors.toList());
    }
    /**
     * 根据IP查找服务，默认端口15000，否者自行填写端口
     * */
    public List<RPCMethodParser.ServiceModel> ipSearch(String ip) {
        try {
            ip = ip.contains(":") ? ip : ip + ":" + CommonConfig.PRODUCER_INVOKE_PORT;
            return template.opsForSet().members(ip).parallelStream().map(RPCMethodParser::methodKey2Model).collect(Collectors.toList());
        }catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据签名获取服务详情
     * */
    public Producer2ServerRequest serviceDetail(String sign) {
        return JSON.parseObject(template.opsForValue().get(sign + INTERFACE_DETAIL_SUFFIX),Producer2ServerRequest.class);
    }

    /**
     * 获取服务的所有服务提供方（ip：port）
     * */
    public List<String> producers(String sign) {
        return new ArrayList<>(Objects.requireNonNull(template.opsForSet().members(sign)));
    }

    /**
     * 关键字匹配算法
     * @param key
     * @param msg 匹配源
     * 暂时最简单的  可以改为kpm算法
     * */
    private boolean match(String key, RPCMethodParser.ServiceModel msg) {
        return msg.getInterfaceName().contains(key);
    }
}
