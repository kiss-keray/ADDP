package com.nix.jingxun.addp.rpc.server.netty;

import com.alipay.remoting.RemotingContext;
import com.nix.jingxun.addp.rpc.common.protocol.AbstractRPCRequestProcessor;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackage;
import org.springframework.stereotype.Component;

/**
 * @author keray
 * @date 2018/12/09 00:00
 */
@Component
public class ProducerRegisterProcessor extends AbstractRPCRequestProcessor<RPCPackage> {
    @Override
    public RPCPackage process(RemotingContext ctx, RPCPackage msg) throws Exception {
        return null;
    }
}
