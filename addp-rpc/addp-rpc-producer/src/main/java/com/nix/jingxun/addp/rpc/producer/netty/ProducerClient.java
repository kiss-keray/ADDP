package com.nix.jingxun.addp.rpc.producer.netty;

import com.alipay.remoting.CommandFactory;
import com.nix.jingxun.addp.rpc.common.RPCRemotingClient;
import com.nix.jingxun.addp.rpc.common.protocol.ARPCCommandFactory;

/**
 * @author keray
 * @date 2018/12/10 21:07
 */
public class ProducerClient extends RPCRemotingClient {

    public static final RPCRemotingClient CLIENT = new ProducerClient();

    protected ProducerClient() {
        super(new ARPCCommandFactory());
        
    }
}
