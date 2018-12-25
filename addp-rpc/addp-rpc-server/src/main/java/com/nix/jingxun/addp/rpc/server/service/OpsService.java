package com.nix.jingxun.addp.rpc.server.service;

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
}
