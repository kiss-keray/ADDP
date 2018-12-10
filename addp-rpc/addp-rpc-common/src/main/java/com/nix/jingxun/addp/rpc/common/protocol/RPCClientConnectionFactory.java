package com.nix.jingxun.addp.rpc.common.protocol;

import com.alipay.remoting.ConnectionEventHandler;
import com.alipay.remoting.codec.Codec;
import com.alipay.remoting.config.ConfigurableInstance;
import com.alipay.remoting.connection.AbstractConnectionFactory;
import io.netty.channel.ChannelHandler;

/**
 * @author Kiss
 * @date 2018/10/21 14:38
 */
public class RPCClientConnectionFactory extends AbstractConnectionFactory {


    public RPCClientConnectionFactory(Codec codec, ChannelHandler heartbeatHandler,
                                      ChannelHandler handler, ConfigurableInstance configInstance) {
        super(codec, heartbeatHandler, handler, configInstance);
    }

    @Override
    public void init(ConnectionEventHandler connectionEventHandler) {
        super.init(connectionEventHandler);
    }
}
