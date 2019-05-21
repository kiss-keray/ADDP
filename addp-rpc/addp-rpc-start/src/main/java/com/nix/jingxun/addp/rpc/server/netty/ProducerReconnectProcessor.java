package com.nix.jingxun.addp.rpc.server.netty;

import com.alipay.remoting.RemotingContext;
import com.nix.jingxun.addp.rpc.common.protocol.AbstractRPCRequestProcessor;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackage;
import com.nix.jingxun.addp.rpc.server.handler.ProducerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author keray
 * @date 2018/12/10 20:48
 */
@Component
public class ProducerReconnectProcessor extends AbstractRPCRequestProcessor<RPCPackage> {
    @Autowired
    private ProducerHandler producerHandler;
    @Override
    public RPCPackage process(RemotingContext ctx, RPCPackage msg) throws Exception {
        if (!producerHandler.reconnect(ctx.getConnection().getChannel())) {
            ctx.getConnection().close();
        }
        return null;
    }
}
