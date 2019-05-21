package com.nix.jingxun.addp.rpc.server.netty;

import com.alipay.remoting.RemotingContext;
import com.nix.jingxun.addp.rpc.common.protocol.AbstractRPCRequestProcessor;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackage;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackageCode;
import com.nix.jingxun.addp.rpc.server.handler.ProducerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author keray
 * @date 2018/12/09 00:04
 */
@Component
public class ConsumerGetMsgProcessor extends AbstractRPCRequestProcessor<RPCPackage> {
    @Autowired
    private ProducerHandler producerHandler;

    @Override
    public RPCPackage process(RemotingContext ctx, RPCPackage msg) throws Exception {
        String key = msg.getObject().toString();
        RPCPackage response = RPCPackage.createMessage(msg.getId(), RPCPackageCode.RESPONSE_SUCCESS);
        response.setObject(producerHandler.consumerGetInterfaceMsg(key));
        return response;
    }
}
