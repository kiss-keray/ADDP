package com.nix.jingxun.addp.rpc.common.serializable;

import com.alibaba.fastjson.JSON;
import com.nix.jingxun.addp.rpc.common.RPCRequest;

/**
 * @author keray
 * @date 2018/12/08 13:14
 */
public class JsonSerializer implements Serializer {
    @Override
    public RPCRequest decoderRequest(String requestStr) throws Exception {
        RPCRequest request = JSON.parseObject(requestStr,RPCRequest.class);
        for (RPCRequest.ParamsData paramsData:request.getParamData()) {
            paramsData.setData(JSON.parseObject(JSON.toJSONString(paramsData.getData()),paramsData.getClazz()));
        }
        return request;
    }

    @Override
    public String encoderRequest(RPCRequest request) {
        return JSON.toJSONString(request);
    }
}
