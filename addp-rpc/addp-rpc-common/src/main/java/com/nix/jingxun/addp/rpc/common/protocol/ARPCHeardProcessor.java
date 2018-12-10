package com.nix.jingxun.addp.rpc.common.protocol;

import com.alipay.remoting.AbstractRemotingProcessor;
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
public class ARPCHeardProcessor extends AbstractRPCRequestProcessor<RPCPackage> {

    @Override
    public RPCPackage process(RemotingContext ctx, RPCPackage msg) throws Exception {
        //请求心跳
        if (msg.getCmdCode() == RPCPackageCode.HEART_SYN_COMMAND) {
            log.debug("收到心跳数据包 {}", RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
            return RPCPackage.createHeardAckMessage();
        }
        //响应心跳
        else if (msg.getCmdCode() == RPCPackageCode.HEART_ACK_COMMAND) {
            log.debug("{} 心跳响应", RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
        }
        return null;
    }
}
