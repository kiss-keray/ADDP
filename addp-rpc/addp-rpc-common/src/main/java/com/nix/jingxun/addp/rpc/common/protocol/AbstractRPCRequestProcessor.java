package com.nix.jingxun.addp.rpc.common.protocol;

import com.alipay.remoting.AbstractRemotingProcessor;
import com.alipay.remoting.RemotingContext;
import com.alipay.remoting.util.RemotingUtil;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @author keray
 * @date 2018/11/06 下午7:52
 */
@Slf4j
public abstract class AbstractRPCRequestProcessor<A extends RPCPackage> extends AbstractRemotingProcessor<A> {
    @Override
    public void doProcess(RemotingContext ctx, A msg) throws Exception {
        RPCPackage responseMessage = process(ctx, msg);
        if (responseMessage != null) {
            ctx.getChannelContext().writeAndFlush(responseMessage).addListener((ChannelFutureListener) channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    log.warn("response fail :{}", RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
                }
            });
        }
    }

    /**
     * 处理请求数据包 返回请求
     *
     * @param ctx
     * @param msg
     * @return
     */
    public abstract RPCPackage process(RemotingContext ctx, A msg) throws Exception;
}
