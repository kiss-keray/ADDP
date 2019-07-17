package com.nix.jingxun.addp.rpc.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.nix.jingxun.addp.rpc.common.Producer2ServerRequest;
import com.nix.jingxun.addp.rpc.common.RPCMethodParser;
import com.nix.jingxun.addp.rpc.common.protocol.RPCRequest;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackage;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackageCode;
import com.nix.jingxun.addp.rpc.common.util.FluentMaps;
import com.nix.jingxun.addp.rpc.server.handler.ProducerHandler;
import com.nix.jingxun.addp.rpc.server.netty.ServerClient;
import com.nix.jingxun.addp.rpc.server.service.OpsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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

    @Override
    public Object methodInvoke(String interfaceName, String methodName, String[] paramTypes, Object[] data,String appName,String group,String version) {
        RPCPackage rpcPackage = RPCPackage.createRequestMessage(RPCPackageCode.RPC_INVOKE);
        Map<String,Object> json = new HashMap<>(32);
        Map<String,Object> request = new HashMap<>(32);
        List<Map<String,String>> paramData = new ArrayList<>(32);
        request.put("interfaceName",interfaceName);
        request.put("method",methodName);
        request.put("timeout",10000);
        request.put("date",new Date());
        request.put("paramData",paramData);
        for (int i = 0;i < data.length;i ++) {
            paramData.add(FluentMaps.newMap("clazz",paramTypes[i],"data",data[i].toString()));
        }
        request.put("methodParamTypes",paramTypes);
        json.put("clazz",RPCRequest.class);
        json.put("data",request);
        rpcPackage.setContent(JSON.toJSONString(json).getBytes());
        try {
            return ServerClient.CLIENT.invokeSync(producerHandler.consumerGetInterfaceMsg(RPCMethodParser.getMethodKey(new RPCMethodParser.ServiceModel(interfaceName, appName, group, version))),rpcPackage,10000);
        } catch (Exception e) {
            e.printStackTrace();
            return e;
        }
    }
}
