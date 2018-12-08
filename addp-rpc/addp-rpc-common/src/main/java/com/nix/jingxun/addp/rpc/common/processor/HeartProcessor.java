package com.nix.jingxun.addp.rpc.common.processor;

import com.nix.jingxun.addp.rpc.common.config.CommandCode;
import com.nix.jingxun.addp.rpc.remoting.netty.NettyRequestProcessor;
import com.nix.jingxun.addp.rpc.remoting.protocol.RemotingCommand;
import com.nix.jingxun.addp.rpc.remoting.protocol.RemotingSysResponseCode;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author keray
 * @date 2018/12/07 20:12
 */
@Slf4j
@Component
public class HeartProcessor implements NettyRequestProcessor {
    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        log.info("heart request {}",request);
        RemotingCommand remotingCommand = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, CommandCode.HELLO.getDesc());
        remotingCommand.setOpaque(request.getOpaque());
        return remotingCommand;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
