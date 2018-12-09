package com.nix.jingxun.addp.rpc.server.netty;

import com.nix.jingxun.addp.rpc.common.RPCRemotingServer;
import com.nix.jingxun.addp.rpc.common.protocol.ARPCProtocolV1;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackageCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author keray
 * @date 2018/10/19 2:27 PM
 */
@Slf4j
@Component
public class ServerRemotingServer extends RPCRemotingServer {


    private static final int PORT = Integer.parseInt(System.getProperty("rpc.server.port","15100"));

    private ServerRemotingServer() {
        super(PORT,new ServerIdleHandler());
    }

    @PostConstruct
    public void startServer() {
        super.start();
    }

    @Override
    protected void doInit() {
        super.doInit();
        registerProcessor(ARPCProtocolV1.PROTOCOL_CODE, RPCPackageCode.CONSUMER_GET_MSG,new ConsumerGetMsgProcessor());
        registerProcessor(ARPCProtocolV1.PROTOCOL_CODE, RPCPackageCode.PRODUCER_REGISTER,new ProducerRegisterProcessor());
    }

}
