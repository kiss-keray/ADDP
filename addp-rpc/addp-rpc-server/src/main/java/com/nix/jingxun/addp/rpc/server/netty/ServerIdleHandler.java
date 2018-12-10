package com.nix.jingxun.addp.rpc.server.netty;

import com.alipay.remoting.util.RemotingUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author keray
 * @date 2018/12/09 13:09
 */
@ChannelHandler.Sharable
@Slf4j
public class ServerIdleHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            try {
                log.warn("Connection idle, close it from server side: {}", RemotingUtil.parseRemoteAddress(ctx.channel()));
                ctx.close();
            } catch (Exception e) {
                log.warn("Exception caught when closing connection in ServerIdleHandler.", e);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
