package com.nix.jingxun.addp.rpc.producer.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alipay.remoting.RemotingContext;
import com.nix.jingxun.addp.rpc.common.RPCRequest;
import com.nix.jingxun.addp.rpc.common.RPCResponse;
import com.nix.jingxun.addp.rpc.common.protocol.AbstractRPCRequestProcessor;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackage;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackageCode;
import com.nix.jingxun.addp.rpc.common.serializable.JsonSerializer;
import com.nix.jingxun.addp.rpc.producer.InvokeContainer;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author keray
 * @date 2018/12/07 21:11
 */
@Slf4j
public class RPCInvokeProcessor extends AbstractRPCRequestProcessor<RPCPackage> {
    private RPCResponse invoke(RPCRequest request) {
        RPCResponse response = new RPCResponse();
        try {
            response.setContext(request.getContext());
            Class<?> clazz = InvokeContainer.getImpl(request.getInterfaceName()).getClass();
            Class[] methodParamSign = request.getMethodParamTypes();
            Method method;
            if (methodParamSign == null) {
                method = clazz.getMethod(request.getMethod());
            } else {
                method = clazz.getMethod(request.getMethod(), methodParamSign);
            }
            Object result = method.invoke(InvokeContainer.getImpl(request.getInterfaceName()), request.getParams());
            response.setCode(RPCResponse.ResponseCode.SUCCESS);
            if (result != null) {
                response.setResult(new RPCResponse.SuccessResult(result.getClass(), result));
            }
        } catch (Exception e) {
            response.setCode(RPCResponse.ResponseCode.ERROR);
            response.setError(new RPCResponse.ErrorResult(RPCResponse.ResponseError.EXCEPTION, e));
        }
        return response;
    }

    @Override
    public RPCPackage process(RemotingContext ctx, RPCPackage msg) throws Exception {
        log.info("rpc invoke {}", msg);
        RPCRequest request = (RPCRequest) msg.getObject();
        if (request.getParamData() != null) {
            for (int i = 0;i < request.getMethodParamTypes().length;i ++ ) {
                try {
                    request.getParamData().get(i).setData(JSON.parseObject(JSON.toJSONString(request.getParamData().get(i).getData()), request.getParamData().get(i).getClazz()));
                }catch (JSONException e) {
                    request.getParamData().get(i).setData(JSON.parseObject(JSON.toJSONString(request.getParamData().get(i).getData()), request.getMethodParamTypes()[i]));
                }
            }
        }
        RPCPackage responsePackage = RPCPackage.createMessage(msg.getId(), RPCPackageCode.RESPONSE_SUCCESS);
        RPCResponse response = invoke(request);
        responsePackage.setObject(response);
        return responsePackage;
    }
}
