package com.nix.jingxun.addp.rpc.server.netty;

import com.alipay.remoting.RemotingContext;
import com.nix.jingxun.addp.rpc.common.protocol.AbstractRPCRequestProcessor;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackage;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

/**
 * @author keray
 * @date 2018/12/09 00:04
 */
@Component
public class ConsumerGetMsgProcessor extends AbstractRPCRequestProcessor<RPCPackage> {

    @Override
    public RPCPackage process(RemotingContext ctx, RPCPackage msg) throws Exception {
        return null;
    }
}
