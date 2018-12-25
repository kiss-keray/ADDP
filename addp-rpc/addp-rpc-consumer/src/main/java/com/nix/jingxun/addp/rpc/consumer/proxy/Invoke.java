package com.nix.jingxun.addp.rpc.consumer.proxy;

import com.alibaba.fastjson.JSON;
import com.alipay.remoting.exception.RemotingException;
import com.nix.jingxun.addp.rpc.common.RPCInterfaceAnnotation;
import com.nix.jingxun.addp.rpc.common.RPCMethodParser;
import com.nix.jingxun.addp.rpc.common.RPCRequest;
import com.nix.jingxun.addp.rpc.common.RPCResponse;
import com.nix.jingxun.addp.rpc.common.config.CommonConfig;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackage;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackageCode;
import com.nix.jingxun.addp.rpc.consumer.RPCContext;
import com.nix.jingxun.addp.rpc.consumer.netty.ConsumerClient;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author keray
 * @date 2018/12/14 17:42
 */
@Slf4j
public class Invoke {
    public static Object invoke(Object _this,String methodName,Object[] args,String[] methodParamTypes) {
        try {
            Class<?> proxyInterface = _this.getClass().getInterfaces()[0];
            RPCInterfaceAnnotation consumer = _this.getClass().getAnnotation(RPCInterfaceAnnotation.class);
            if (consumer == null) {
                throw new RuntimeException("非rpc代理接口 执行失败");
            }
            // 到注册中心去找服务提供方法
            String producerHost = getProducerHost(RPCMethodParser.getMethodKey(new RPCMethodParser.ServiceModel(proxyInterface.getName(), consumer.appName(), consumer.group(), consumer.version())));
            RPCPackage responsePackage = null;
            RPCRequest request = createInvokeRPCRequest(proxyInterface, methodName, args);
            request.setMethodParamTypes(methodParamTypes);
            RPCPackage rpcPackage = RPCPackage.createRequestMessage(RPCPackageCode.RPC_INVOKE);
            rpcPackage.setObject(request);
            switch (consumer.type()) {
                case SYNC_EXEC_METHOD: {
                    if (consumer.timeout() == 0) {
                        throw new RuntimeException("同步rpc调用timeout 必须大于 0");
                    }
                    responsePackage = ConsumerClient.CLIENT.invokeSync(producerHost, rpcPackage, consumer.timeout());
                }
                break;
                case ASYNC_EXEC_METHOD:
                    return ConsumerClient.CLIENT.invokeWithFuture(producerHost, responsePackage, 0);
                default:
                    break;
            }
            if (responsePackage.getCmdCode() == RPCPackageCode.RESPONSE_SUCCESS) {
                RPCResponse rpcResponse = (RPCResponse) responsePackage.getObject();
                if (rpcResponse.getResult() != null) {
                    rpcResponse.getResult().setData(JSON.parseObject(JSON.toJSONString(rpcResponse.getResult().getData()), rpcResponse.getResult().getClazz()));
                }
                if (rpcResponse.getCode() == RPCResponse.ResponseCode.SUCCESS) {
                    if (rpcResponse.getResult() != null) {
                        return rpcResponse.getResult().getData();
                    }
                    return null;
                } else {
                    throw rpcResponse.getError().getException();
                }
            } else {
                throw new RuntimeException("rpc调用失败");
            }
        }catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    private static RPCRequest createInvokeRPCRequest(Class<?> proxyInterface, String method, Object[] args) {
        RPCRequest request = new RPCRequest();
        request.setContext(RPCContext.getContext());
        request.setInterfaceName(proxyInterface.getName());
        request.setMethod(method);
        request.setDate(new Date());
        if (args != null && args.length > 0) {
            RPCRequest.ParamsData[] paramsData = new RPCRequest.ParamsData[args.length];
            for (int i = 0;i < args.length;i ++) {
                if (args[i] == null) {
                    paramsData[i] = new RPCRequest.ParamsData(null,null);
                } else {
                    paramsData[i] = new RPCRequest.ParamsData(args[i].getClass(),args[i]);
                }
            }
            request.setParamData(paramsData);
        }
        return request;
    }

    private static String getProducerHost(String interfaceKey) throws RemotingException, InterruptedException {
        log.info("rpc 调用 {}", interfaceKey);
        RPCPackage request = RPCPackage.createRequestMessage(RPCPackageCode.CONSUMER_GET_MSG);
        request.setObject(interfaceKey);
        return ConsumerClient.CLIENT.invokeSync(CommonConfig.SERVER_HOST, request, 1000).getObject().toString();
    }
}
