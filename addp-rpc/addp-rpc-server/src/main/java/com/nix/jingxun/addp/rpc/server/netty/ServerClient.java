package com.nix.jingxun.addp.rpc.server.netty;

import com.alipay.remoting.CommandFactory;
import com.nix.jingxun.addp.rpc.common.RPCRemotingClient;
import com.nix.jingxun.addp.rpc.common.protocol.ARPCCommandFactory;

/**
 * @author keray
 * @date 2018/12/10 21:13
 */
public class ServerClient extends RPCRemotingClient {
    public static final RPCRemotingClient CLIENT = new ServerClient();

    protected ServerClient() {
        super(new ARPCCommandFactory());
    }
}
