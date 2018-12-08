package com.nix.jingxun.addp.rpc.producer.netty;
import com.alibaba.fastjson.JSON;
import com.nix.jingxun.addp.rpc.common.RPCRequest;
import com.nix.jingxun.addp.rpc.common.RPCResponse;
import com.nix.jingxun.addp.rpc.common.serializable.Serializer;
import com.nix.jingxun.addp.rpc.producer.InvokeContainer;
import com.nix.jingxun.addp.rpc.remoting.netty.NettyRequestProcessor;
import com.nix.jingxun.addp.rpc.remoting.protocol.RemotingCommand;
import com.nix.jingxun.addp.rpc.remoting.protocol.RemotingSysResponseCode;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author keray
 * @date 2018/12/07 21:11
 */
@Component
public class RPCInvokeProcessor implements NettyRequestProcessor {
    @Resource(name = "jsonSerializer")
    private Serializer serializer;
    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        String json = new String(request.getBody());
        RPCResponse response = new RPCResponse();
        try {
            RPCRequest req = serializer.decoderRequest(json);
            Class<?> clazz = InvokeContainer.getImpl(req.getInterfaceName()).getClass();
            Class[] methodParamSign = req.getMethodParamTypes();
            Method method;
            if (methodParamSign == null) {
                method = clazz.getMethod(req.getMethod());
            } else {
                method = clazz.getMethod(req.getMethod(),methodParamSign);
            }
            Object result = method.invoke(InvokeContainer.getImpl(req.getInterfaceName()),req.getParams());
            response.setCode(RPCResponse.ResponseCode.SUCCESS);
            if (result != null) {
                response.setResult(new RPCResponse.SuccessResult(result.getClass(), result));
            }
        }catch (Exception e) {
           response.setCode(RPCResponse.ResponseCode.ERROR);
           response.setError(new RPCResponse.ErrorResult(RPCResponse.ResponseError.EXCEPTION,e));
        }
        RemotingCommand responseCommand = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS,null);
        responseCommand.setOpaque(request.getOpaque());
        responseCommand.setBody(serializer.encoderResponse(response).getBytes());
        return responseCommand;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
