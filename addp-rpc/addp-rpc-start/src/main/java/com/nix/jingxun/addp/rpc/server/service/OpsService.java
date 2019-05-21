package com.nix.jingxun.addp.rpc.server.service;

import com.nix.jingxun.addp.rpc.common.Producer2ServerRequest;
import com.nix.jingxun.addp.rpc.common.RPCMethodParser;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author keray
 * @date 2018/12/25 15:15
 */
@Service
public interface OpsService {
    enum SearchType{
        service,
        app,
        ip
    }
    /**
     * 搜索rpc服务
     * @param key
     * @param type
     * @return
     * */
    List<RPCMethodParser.ServiceModel> search(String key,SearchType type);

    /**
     * 获取服务接口的详情
     * @param sign 服务接口签名
     * @return
     * */
    Producer2ServerRequest serviceDetail(String sign);

    /**
     * 获取服务提供方集合
     * */
    List<String> producers(String sign);

    /**
     * rpc方法测试
     * */
    Object methodInvoke(String interfaceName,String methodName, String[] paramTypes,Object[] data,String appName,String group,String version);
}
