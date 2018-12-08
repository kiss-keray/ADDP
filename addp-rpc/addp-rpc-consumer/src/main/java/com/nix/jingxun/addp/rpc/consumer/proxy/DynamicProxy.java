package com.nix.jingxun.addp.rpc.consumer.proxy;

import com.nix.jingxun.addp.rpc.common.RPCRequest;
import com.nix.jingxun.addp.rpc.common.RPCResponse;
import com.nix.jingxun.addp.rpc.common.client.NettyClient;
import com.nix.jingxun.addp.rpc.common.config.CommandCode;
import com.nix.jingxun.addp.rpc.common.serializable.Serializer;
import com.nix.jingxun.addp.rpc.consumer.RPCContext;
import com.nix.jingxun.addp.rpc.common.RPCInterfaceAnnotation;
import com.nix.jingxun.addp.rpc.remoting.protocol.RemotingCommand;
import com.nix.jingxun.addp.rpc.remoting.protocol.RemotingSysResponseCode;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author keray
 * @date 2018/12/07 22:39
 */
@Component
public class DynamicProxy implements InvocationHandler {

    @Resource(name = "jsonSerializer")
    Serializer serializer;
    @Override
    public Object invoke(Object object, Method method, Object[] args) throws Throwable{
        Class<?> proxyInterface = method.getDeclaringClass();
        RPCInterfaceAnnotation consumer = proxyInterface.getAnnotation(RPCInterfaceAnnotation.class);
        if (consumer == null) {
            throw new RuntimeException("非rpc代理接口 执行失败");
        }
        RemotingCommand responseCommand = null;
        String appName = consumer.appName();
        String group = consumer.group();
        String version = consumer.version();
        // 到注册中心去找服务提供方法
        String producerHost = "127.0.0.1:15000";
        CommandCode type = consumer.type();
        long timeout = consumer.timeout();
        RPCRequest request = createRequest(proxyInterface,method,args);
        RemotingCommand command = RemotingCommand.createRequestCommand(type.getCode(),type.getDesc());
        command.setBody(serializer.encoderRequest(request).getBytes());
        switch (type) {
            case SYNC_EXEC_METHOD:{
                if (timeout == 0) {
                    throw new RuntimeException("同步rpc调用timeout 必须大于 0");
                }
                responseCommand = NettyClient.invokeSync(producerHost,command,timeout);
            }break;
            case ASYNC_EXEC_METHOD:return NettyClient.invokeAsync(producerHost,command);
            default:break;
        }
        if (responseCommand.getCode() == RemotingSysResponseCode.SUCCESS) {
            RPCResponse rpcResponse = serializer.decoderResponse(new String(responseCommand.getBody()));
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
    }
    private RPCRequest createRequest(Class<?> proxyInterface, Method method, Object[] args) {
        RPCRequest request = new RPCRequest();
        request.setContext(RPCContext.getContext());
        request.setInterfaceName(proxyInterface.getName());
        request.setMethod(method.getName());
        request.setDate(new Date());
        if (args != null && args.length > 0) {
            request.setParamData(Stream.of(args).map(item -> new RPCRequest.ParamsData(item.getClass(), item)).collect(Collectors.toList()));
        }
        return request;
    }

}