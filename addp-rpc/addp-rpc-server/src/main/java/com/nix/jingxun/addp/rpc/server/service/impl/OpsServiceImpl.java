package com.nix.jingxun.addp.rpc.server.service.impl;

import com.nix.jingxun.addp.rpc.common.Producer2ServerRequest;
import com.nix.jingxun.addp.rpc.common.RPCMethodParser;
import com.nix.jingxun.addp.rpc.server.handler.ProducerHandler;
import com.nix.jingxun.addp.rpc.server.service.OpsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author keray
 * @date 2018/12/25 17:24
 */
@Service
public class OpsServiceImpl implements OpsService {
    @Resource
    private ProducerHandler producerHandler;
    @Override
    public List<RPCMethodParser.ServiceModel> search(String key, SearchType type) {
        switch (type) {
            case service:return producerHandler.serviceSearch(key);
            case ip:return producerHandler.ipSearch(key);
            case app:return producerHandler.appSearch(key);
            default:return null;
        }
    }

    @Override
    public Producer2ServerRequest serviceDetail(String sign) {
        return producerHandler.serviceDetail(sign);
    }

    @Override
    public List<String> producers(String sign) {
        return producerHandler.producers(sign);
    }
}
