package com.nix.jingxun.addp.rpc.common.protocol;

import com.alipay.remoting.RemotingContext;
import com.alipay.remoting.RemotingProcessor;
import com.alipay.remoting.util.RemotingUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

/**
 * @author Kiss
 * @date 2018/10/21 17:24
 */
@Slf4j
public class ARPCHeardProcessor implements RemotingProcessor<RPCPackage> {
    @Override
    public void process(RemotingContext ctx, RPCPackage msg, ExecutorService defaultExecutor) throws Exception {
        //请求心跳
        if (msg.getCmdCode() == RPCPackageCode.HEART_SYN_COMMAND) {
            log.debug("收到心跳数据包 {}",RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
            ctx.getChannelContext().writeAndFlush(RPCPackage.createHeardAckMessage());
        }
        //响应心跳
        else if (msg.getCmdCode() == RPCPackageCode.HEART_ACK_COMMAND) {
            log.debug("{} 心跳响应", RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
        }
    }

    @Override
    public ExecutorService getExecutor() {
        return null;
    }

    @Override
    public void setExecutor(ExecutorService executor) {

    }
}
