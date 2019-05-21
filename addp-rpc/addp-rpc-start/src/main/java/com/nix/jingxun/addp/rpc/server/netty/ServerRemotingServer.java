package com.nix.jingxun.addp.rpc.server.netty;

import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.util.RemotingUtil;
import com.nix.jingxun.addp.rpc.common.RPCRemotingServer;
import com.nix.jingxun.addp.rpc.common.config.CommonConfig;
import com.nix.jingxun.addp.rpc.common.protocol.ARPCProtocolV1;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackageCode;
import com.nix.jingxun.addp.rpc.server.handler.ProducerHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author keray
 * @date 2018/10/19 2:27 PM
 */
@Slf4j
@Component
public class ServerRemotingServer extends RPCRemotingServer {

    @Autowired
    private ProducerRegisterProcessor producerRegisterProcessor;
    @Autowired
    private ConsumerGetMsgProcessor consumerGetMsgProcessor;
    @Autowired
    private ProducerHandler producerHandler;
    @Autowired
    private ProducerReconnectProcessor reconnectProcessor;

    private ServerRemotingServer() {
        super(CommonConfig.SERVER_PORT, new ServerIdleHandler());
    }

    @PostConstruct
    public void startServer() {
        super.start();
    }

    @Override
    protected void doInit() {
        super.doInit();

        //注册channel事件处理器
        ConnectionEventProcessor connectionEventProcessor = (remoteAddr, conn) -> {
            connectionManager.remove(conn);
            producerHandler.producerLeave(conn.getChannel());
            conn.close();
            log.info("连接关闭 {}", RemotingUtil.parseRemoteAddress(conn.getChannel()));
        };
        connectionEventListener.addConnectionEventProcessor(ConnectionEventType.CLOSE, connectionEventProcessor);
        connectionEventListener.addConnectionEventProcessor(ConnectionEventType.EXCEPTION, connectionEventProcessor);
        connectionEventListener.addConnectionEventProcessor(ConnectionEventType.CONNECT, (remoteAddr, conn) -> {
            if (producerHandler.reconnect(conn.getChannel())) {
                log.info("{} 重连成功",conn.getRemoteAddress());
            }
        });
        registerProcessor(ARPCProtocolV1.PROTOCOL_CODE, RPCPackageCode.CONSUMER_GET_MSG, consumerGetMsgProcessor);
        registerProcessor(ARPCProtocolV1.PROTOCOL_CODE, RPCPackageCode.PRODUCER_REGISTER, producerRegisterProcessor);
        registerProcessor(ARPCProtocolV1.PROTOCOL_CODE, RPCPackageCode.PRODUCER_RECON, reconnectProcessor);
    }

}
