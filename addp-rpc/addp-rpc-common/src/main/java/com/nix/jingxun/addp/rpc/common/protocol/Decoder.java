package com.nix.jingxun.addp.rpc.common.protocol;

import com.alipay.remoting.Connection;
import com.alipay.remoting.Protocol;
import com.alipay.remoting.ProtocolCode;
import com.alipay.remoting.ProtocolManager;
import com.alipay.remoting.exception.CodecException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Kiss
 * @date 2018/10/21 10:16
 */
@Slf4j
public class Decoder extends LengthFieldBasedFrameDecoder {
    private static final int FRAME_MAX_LENGTH = 1024 * 1024 * 1024;

    /**
     * by default, suggest design a single byte for protocol version.
     */
    public static final int DEFAULT_PROTOCOL_VERSION_LENGTH = 1;
    /**
     * protocol version should be a positive number, we use -1 to represent illegal
     */
    public static final int DEFAULT_ILLEGAL_PROTOCOL_VERSION_LENGTH = -1;
    /**
     * the length of protocol code
     */
    protected int protocolCodeLength;

    public Decoder(int protocolCodeLength) {
        super(FRAME_MAX_LENGTH, 0, 4, 0, 4);
        this.protocolCodeLength = protocolCodeLength;
    }

    protected ProtocolCode decodeProtocolCode(ByteBuf in) {
        if (in.readableBytes() >= protocolCodeLength) {
            byte[] protocolCodeBytes = new byte[protocolCodeLength];
            in.readBytes(protocolCodeBytes);
            return ProtocolCode.fromBytes(protocolCodeBytes);
        }
        return null;
    }

    protected byte decodeProtocolVersion(ByteBuf in) {
        if (in.readableBytes() >= DEFAULT_PROTOCOL_VERSION_LENGTH) {
            return in.readByte();
        }
        return DEFAULT_ILLEGAL_PROTOCOL_VERSION_LENGTH;
    }


    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = null;
        try {
            frame = (ByteBuf) super.decode(ctx, in);
            if (null == frame) {
                return null;
            }
            ProtocolCode protocolCode = decodeProtocolCode(frame);
            if (null != protocolCode) {
                byte protocolVersion = decodeProtocolVersion(frame);
                if (ctx.channel().attr(Connection.PROTOCOL).get() == null) {
                    ctx.channel().attr(Connection.PROTOCOL).set(protocolCode);
                    if (DEFAULT_ILLEGAL_PROTOCOL_VERSION_LENGTH != protocolVersion) {
                        ctx.channel().attr(Connection.VERSION).set(protocolVersion);
                    }
                }
                Protocol protocol = ProtocolManager.getProtocol(protocolCode);
                if (null != protocol) {
                    List list = new LinkedList();
                    protocol.getDecoder().decode(ctx, frame, list);
                    return list;
                } else {
                    throw new CodecException("Unknown protocol code: [" + protocolCode
                            + "] while decode in ProtocolDecoder.");
                }
            }

        } catch (Exception e) {
            log.error("decode exception", e);
            ctx.close();
        }
        return frame;
    }

}
