package com.nix.jingxun.addp.rpc.consumer.netty;

import com.alibaba.fastjson.JSON;
import com.nix.jingxun.addp.rpc.common.RPCRequest;
import com.nix.jingxun.addp.rpc.common.RPCResponse;
import com.nix.jingxun.addp.rpc.common.client.NettyClient;
import com.nix.jingxun.addp.rpc.common.config.CommandCode;
import com.nix.jingxun.addp.rpc.common.serializable.JsonSerializer;
import com.nix.jingxun.addp.rpc.common.serializable.Serializer;
import com.nix.jingxun.addp.rpc.remoting.exception.RemotingTimeoutException;
import com.nix.jingxun.addp.rpc.remoting.protocol.RemotingCommand;

import java.util.Arrays;
import java.util.Date;

/**
 * @author keray
 * @date 2018/12/07 20:25
 */
public class ConsumerNetty {
    public static void main(String[] args) throws Exception {
        RPCResponse response;
        RemotingCommand responseCommand;
        RemotingCommand command = RemotingCommand.createRequestCommand(CommandCode.SYNC_EXEC_METHOD.getCode(),"hello");
        Serializer serializer = new JsonSerializer();
        RPCRequest request = new RPCRequest();
        request.setInterfaceName("com.nix.jingxun.addp.rpc.producer.Hello");
        request.setMethod("sayHello");
        request.setDate(new Date());
        request.setType(CommandCode.SYNC_EXEC_METHOD);
        request.setParamData(Arrays.asList(new RPCRequest.ParamsData(String.class,"hello consumer")));
        command.setBody(serializer.encoderRequest(request).getBytes());
        responseCommand = NettyClient.invokeSync("127.0.0.1:15000",command, 10000);

        response = serializer.decoderResponse(new String(responseCommand.getBody()));

        System.out.println(JSON.toJSONString(response));

        command = RemotingCommand.createRequestCommand(CommandCode.SYNC_EXEC_METHOD.getCode(),"hello");

        command.setBody(serializer.encoderRequest(request).getBytes());
        request.setMethod("getHello");
        request.setParamData(null);

        command.setBody(serializer.encoderRequest(request).getBytes());
        responseCommand = NettyClient.invokeSync("127.0.0.1:15000",command, 10000);
        response = serializer.decoderResponse(new String(responseCommand.getBody()));

        System.out.println(JSON.toJSONString(response));
    }
}
