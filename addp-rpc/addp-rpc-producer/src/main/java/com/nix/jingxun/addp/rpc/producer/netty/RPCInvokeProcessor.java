package com.nix.jingxun.addp.rpc.producer.netty;

import com.nix.jingxun.addp.rpc.remoting.netty.NettyRequestProcessor;
import com.nix.jingxun.addp.rpc.remoting.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author keray
 * @date 2018/12/07 21:11
 */
public class RPCInvokeProcessor implements NettyRequestProcessor {
    /**
     {
     "interfaceName":"com.nix.xxxx",
     "method":"syaHello",
     "data":[{"clazz":"java.lang.String","data":"hello world"},{"clazz":"com.nix.xxx","data":{"name":"name","password":"***"}},"java.lang.Integer":null],
     "type":"SYNC_EXEC_METHOD" -- CommandCode.SYNC_EXEC_METHOD,
     "timeout":1000,
     "context":{} -- map,
     "source":{"ip":"xx.xx.xx.xx","app":"appName"},
     "date":123323224
     }
     *
     * */
    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        String json = new String(request.getBody());

        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
