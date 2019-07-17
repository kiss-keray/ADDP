package com.nix.jingxun.addp.rpc.consumer.proxy;

import com.alibaba.fastjson.JSON;
import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.ResponseStatus;
import com.alipay.remoting.exception.RemotingException;
import com.nix.jingxun.addp.rpc.common.*;
import com.nix.jingxun.addp.rpc.common.config.CommonConfig;
import com.nix.jingxun.addp.rpc.common.exception.RpcRuntimeException;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackage;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackageCode;
import com.nix.jingxun.addp.rpc.common.protocol.RPCRequest;
import com.nix.jingxun.addp.rpc.common.protocol.RPCResponse;
import com.nix.jingxun.addp.rpc.common.util.CommonUtil;
import com.nix.jingxun.addp.rpc.consumer.netty.ConsumerClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author keray
 * @date 2018/12/14 17:42
 */
@Slf4j
public class Invoke {

    /**
     * 消费方同步执行rpc
     * */
    public static Object invoke(Object _this,String methodName,Object[] args,String[] methodParamTypes) {
        return invoke(_this, methodName, args, methodParamTypes,RPCType.SYNC_EXEC_METHOD,null);
    }
    /**
     * 异步执行rpc
     * */
    public static void invoke(Object _this, String methodName, Object[] args, String[] methodParamTypes, InvokeCallback callback) throws InterruptedException {
        invoke(_this, methodName, args, methodParamTypes,RPCType.ASYNC_EXEC_METHOD,callback);
    }


    private static Object invoke(Object _this, String methodName, Object[] args, String[] methodParamTypes, RPCType rpcType,InvokeCallback callback) {
        try {
            Class<?> proxyInterface = _this.getClass().getInterfaces()[0];
            RPCInterfaceAnnotation consumer = _this.getClass().getAnnotation(RPCInterfaceAnnotation.class);
            RPCPackage rpcPackage = getRequestPack(_this, methodName, args, methodParamTypes);
            // 到注册中心去找服务提供方法
            String producerHost = getProducerHost(RPCMethodParser.getMethodKey(new RPCMethodParser.ServiceModel(proxyInterface.getName(), consumer.appName(), consumer.group(), consumer.version())));
            switch (rpcType) {
                case SYNC_EXEC_METHOD: {
                    RPCPackage responsePackage = ConsumerClient.CLIENT.invokeSync(producerHost, rpcPackage, 3000);
                    if (responsePackage.getCmdCode() == RPCPackageCode.RESPONSE_SUCCESS) {
                        RPCResponse rpcResponse =  responsePackage.coverResponse();
                        if (rpcResponse.getStatus() == ResponseStatus.SUCCESS) {
                            if (rpcResponse.getResult() != null) {
                                return rpcResponse.getResult().getData();
                            }
                            return null;
                        }else if (rpcResponse.getStatus() == ResponseStatus.SERVER_EXCEPTION) {
                            throw rpcResponse.getError().getException();
                        } else {
                            if (rpcResponse.getError() != null && rpcResponse.getError().getException() != null) {
                                throw new RpcRuntimeException(rpcResponse.getError().getException());
                            } else {
                                throw new RpcRuntimeException("rpc调用失败");
                            }
                        }
                    } else {
                        throw new RpcRuntimeException("rpc调用失败");
                    }
                }
                case ASYNC_EXEC_METHOD:
                    ConsumerClient.CLIENT.invokeWithCallback(producerHost, rpcPackage,callback, 3000);
                default:
                    break;
            }
        }catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    private static String getProducerHost(String interfaceKey) throws RemotingException, InterruptedException {
        log.info("rpc 调用 {}", interfaceKey);
        RPCPackage request = RPCPackage.createRequestMessage(RPCPackageCode.CONSUMER_GET_MSG);
        request.setObject(interfaceKey);
        if (CommonConfig.NO_CENTER_SERVER) {
            String[] producers = CommonConfig.STATIC_PRODUCER_HOST;
            return producers[0];
        }
        return ConsumerClient.CLIENT.invokeSync(CommonConfig.SERVER_HOST, request, 1000).getObject().toString();
    }

    private static RPCPackage getRequestPack (Object _this,String methodName,Object[] args,String[] methodParamTypes) {
        Class<?> proxyInterface = _this.getClass().getInterfaces()[0];
        RPCInterfaceAnnotation consumer = _this.getClass().getAnnotation(RPCInterfaceAnnotation.class);
        if (consumer == null) {
            throw new RpcRuntimeException("非rpc代理接口 执行失败");
        }
        RPCRequest request = CommonUtil.createInvokeRPCRequest(proxyInterface.getName(), methodName, args);
        request.setMethodParamTypes(methodParamTypes);
        RPCPackage rpcPackage = RPCPackage.createRequestMessage(RPCPackageCode.RPC_INVOKE);
        rpcPackage.setObject(request);
        return rpcPackage;
    }
}
