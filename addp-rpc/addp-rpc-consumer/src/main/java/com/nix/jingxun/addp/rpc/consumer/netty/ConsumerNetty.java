package com.nix.jingxun.addp.rpc.consumer.netty;

import com.nix.jingxun.addp.rpc.common.client.NettyClient;
import com.nix.jingxun.addp.rpc.common.config.CommandCode;
import com.nix.jingxun.addp.rpc.remoting.exception.RemotingTimeoutException;
import com.nix.jingxun.addp.rpc.remoting.protocol.RemotingCommand;

/**
 * @author keray
 * @date 2018/12/07 20:25
 */
public class ConsumerNetty {
    public static void main(String[] args) throws RemotingTimeoutException {
        System.out.println(NettyClient.invokeSync("127.0.0.1:15000", RemotingCommand.createRequestCommand(CommandCode.HELLO.getCode(),"hello"),1000));
    }
}
