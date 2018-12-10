package com.nix.jingxun.addp.rpc.common.protocol;

import com.alipay.remoting.CommandEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author keray
 * @date 2018/10/19 4:14 PM
 */
@Slf4j
public class ARPCEncoder implements CommandEncoder {
    /**
     * Encode object into bytes.
     *
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    public void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
        try {
            if (msg instanceof RPCPackage) {
                RPCPackage cmd = (RPCPackage) msg;
                log.debug("encode {}", msg);
                // 序列化
                cmd.serialize();
                // 写入数据包长度
                out.writeInt(ARPCProtocolV1.HEADER_DATA_LEN + cmd.getContent().length);
                //写入协议code
                out.writeByte(ARPCProtocolV1.PROTOCOL_CODE);
                //写入协议version
                out.writeByte(ARPCProtocolV1.VERSION);
                //写入command类型
                out.writeShort(cmd.getCmdCode().value());
                //写入message id
                out.writeInt(cmd.getId());
                //写入content长度
                out.writeInt(cmd.getContent().length);
                //写入content
                out.writeBytes(cmd.getContent());
            } else {
                String warnMsg = "msg type [" + msg.getClass() + "] is not subclass of RpcCommand";
                log.warn(warnMsg);
            }
        } catch (Exception e) {
            log.error("Exception caught!", e);
            throw e;
        }
    }
}
