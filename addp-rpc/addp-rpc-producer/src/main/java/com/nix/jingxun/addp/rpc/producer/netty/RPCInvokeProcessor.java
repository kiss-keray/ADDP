package com.nix.jingxun.addp.rpc.producer.netty;
import com.nix.jingxun.addp.rpc.common.RPCRequest;
import com.nix.jingxun.addp.rpc.common.RPCResponse;
import com.nix.jingxun.addp.rpc.common.serializable.JsonSerializer;
import com.nix.jingxun.addp.rpc.common.serializable.Serializer;
import com.nix.jingxun.addp.rpc.producer.InvokeContainer;
import com.nix.jingxun.addp.rpc.remoting.netty.NettyRequestProcessor;
import com.nix.jingxun.addp.rpc.remoting.protocol.RemotingCommand;
import com.nix.jingxun.addp.rpc.remoting.protocol.RemotingSysResponseCode;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @author keray
 * @date 2018/12/07 21:11
 */
public class RPCInvokeProcessor implements NettyRequestProcessor {
    private Serializer serializer = new JsonSerializer();
    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        RemotingCommand responseCommand = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS,null);
        responseCommand.setOpaque(request.getOpaque());
        responseCommand.setBody(serializer.encoderResponse(invoke(serializer.decoderRequest(new String(request.getBody())))).getBytes());
        return responseCommand;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

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
                method = clazz.getMethod(request.getMethod(),methodParamSign);
            }
            Object result = method.invoke(InvokeContainer.getImpl(request.getInterfaceName()),request.getParams());
            response.setCode(RPCResponse.ResponseCode.SUCCESS);
            if (result != null) {
                response.setResult(new RPCResponse.SuccessResult(result.getClass(), result));
            }
        }catch (Exception e) {
            response.setCode(RPCResponse.ResponseCode.ERROR);
            response.setError(new RPCResponse.ErrorResult(RPCResponse.ResponseError.EXCEPTION,e));
        }
        return response;
    }
}
