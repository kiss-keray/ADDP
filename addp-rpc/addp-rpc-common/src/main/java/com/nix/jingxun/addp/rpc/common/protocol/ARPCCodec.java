package com.nix.jingxun.addp.rpc.common.protocol;

import com.alipay.remoting.ProtocolCode;
import com.alipay.remoting.codec.Codec;
import com.alipay.remoting.codec.ProtocolCodeBasedEncoder;
import io.netty.channel.ChannelHandler;

/**
 * @author keray
 * @date 2018/10/19 4:48 PM
 */
public class ARPCCodec implements Codec {

    private final ChannelHandler encoder = new ProtocolCodeBasedEncoder(ProtocolCode.fromBytes(ARPCProtocolV1.PROTOCOL_CODE));

    @Override
    public ChannelHandler newEncoder() {
        return encoder;
    }

    @Override
    public ChannelHandler newDecoder() {
        return new Decoder(ARPCProtocolV1.HEADER_LEN);
    }
}
