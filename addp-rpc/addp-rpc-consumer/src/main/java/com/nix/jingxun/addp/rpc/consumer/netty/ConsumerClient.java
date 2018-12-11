package com.nix.jingxun.addp.rpc.consumer.netty;

import com.alipay.remoting.CommandFactory;
import com.nix.jingxun.addp.rpc.common.RPCRemotingClient;
import com.nix.jingxun.addp.rpc.common.protocol.ARPCCommandFactory;

/**
 * @author keray
 * @date 2018/12/10 21:05
 */
public class ConsumerClient extends RPCRemotingClient {
    public static final RPCRemotingClient CLIENT = new ConsumerClient();

    public ConsumerClient() {
        super(new ARPCCommandFactory());
    }
}
