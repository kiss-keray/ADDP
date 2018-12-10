package com.nix.jingxun.addp.rpc.common.protocol;

import com.alipay.remoting.HeartbeatTrigger;
import com.alipay.remoting.util.RemotingUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author keray
 * @date 2018/10/19 4:22 PM
 */
@Slf4j
public class ARPCHeartbeatTrigger implements HeartbeatTrigger {
    @Override
    public void heartbeatTriggered(ChannelHandlerContext ctx) throws Exception {
        log.info("心跳检测 url={}", RemotingUtil.parseRemoteAddress(ctx.channel()));
        ctx.writeAndFlush(RPCPackage.createHeardSynMessage());
    }
}
