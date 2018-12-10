package com.nix.jingxun.addp.rpc.server.netty;

import com.alipay.remoting.RemotingContext;
import com.nix.jingxun.addp.rpc.common.Producer2ServerRequest;
import com.nix.jingxun.addp.rpc.common.protocol.AbstractRPCRequestProcessor;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackage;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackageCode;
import com.nix.jingxun.addp.rpc.server.handler.ProducerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author keray
 * @date 2018/12/09 00:00
 */
@Component
public class ProducerRegisterProcessor extends AbstractRPCRequestProcessor<RPCPackage> {

    @Autowired
    private ProducerHandler producerHandler;

    @Override
    public RPCPackage process(RemotingContext ctx, RPCPackage msg) throws Exception {
        if (producerHandler.registerInterface((Producer2ServerRequest) msg.getObject(), ctx.getChannelContext().channel())) {
            return RPCPackage.createMessage(msg.getId(), RPCPackageCode.RESPONSE_SUCCESS);
        }
        return RPCPackage.createMessage(msg.getId(), RPCPackageCode.RESPONSE_ERROR);
    }
}
