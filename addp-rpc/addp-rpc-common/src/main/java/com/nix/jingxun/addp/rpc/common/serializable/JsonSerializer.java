package com.nix.jingxun.addp.rpc.common.serializable;

import com.alibaba.fastjson.JSON;
import com.nix.jingxun.addp.rpc.common.RPCRequest;
import com.nix.jingxun.addp.rpc.common.RPCResponse;
import org.springframework.stereotype.Component;

/**
 * @author keray
 * @date 2018/12/08 13:14
 */
@Component("jsonSerializer")
public class JsonSerializer implements Serializer {
    @Override
    public RPCRequest decoderRequest(String requestStr) throws Exception {
        RPCRequest request = JSON.parseObject(requestStr,RPCRequest.class);
        if (request.getParamData() != null) {
            for (RPCRequest.ParamsData paramsData : request.getParamData()) {
                paramsData.setData(JSON.parseObject(JSON.toJSONString(paramsData.getData()), paramsData.getClazz()));
            }
        }
        return request;
    }

    @Override
    public String encoderRequest(RPCRequest request) throws Exception{
        return JSON.toJSONString(request);
    }

    @Override
    public RPCResponse decoderResponse(String responseStr) throws Exception {
        RPCResponse response = JSON.parseObject(responseStr,RPCResponse.class);
        if (response.getResult() != null) {
            response.getResult().setData(JSON.parseObject(JSON.toJSONString(response.getResult().getData()),response.getResult().getClazz()));
        }
        return response;
    }

    @Override
    public String encoderResponse(RPCResponse response) throws Exception {
        return JSON.toJSONString(response);
    }
}
