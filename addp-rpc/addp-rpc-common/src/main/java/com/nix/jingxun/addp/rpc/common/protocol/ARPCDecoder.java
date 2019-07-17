package com.nix.jingxun.addp.rpc.common.protocol;

import com.alipay.remoting.CommandDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author keray
 * @date 2018/10/19 4:14 PM
 */
@Slf4j
public class ARPCDecoder implements CommandDecoder {
    /**
     * DecommandCode bytes into object.
     *
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            short commandCode = in.readShort();
            int id = in.readInt();
            RPCPackage message = RPCPackage.createRequestMessage(RPCPackageCode.valueOfCode(commandCode));
            message.setId(id);
            byte[] content = new byte[in.readInt()];
            if (content.length > 0) {
                in.readBytes(content);
                message.setContent(content);
                message.deserialize();
            }
            log.debug("decode : {}", message);
            out.add(message);
        } catch (Exception e) {
            log.error("decode error ", e);
        }
    }
}
