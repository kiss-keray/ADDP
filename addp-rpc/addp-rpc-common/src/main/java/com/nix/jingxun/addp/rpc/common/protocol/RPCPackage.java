package com.nix.jingxun.addp.rpc.common.protocol;

import com.alipay.remoting.CommandCode;
import com.alipay.remoting.InvokeContext;
import com.alipay.remoting.ProtocolCode;
import com.alipay.remoting.RemotingCommand;
import com.alipay.remoting.config.switches.ProtocolSwitch;
import com.alipay.remoting.exception.DeserializationException;
import com.alipay.remoting.exception.SerializationException;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author keray
 * @date 2018/12/09 00:17
 */
@Data
public class RPCPackage implements RemotingCommand {
    private static final AtomicInteger NEXT_ID = new AtomicInteger(0);

    public RPCPackage() {
        this.id = NEXT_ID.getAndIncrement();
    }
    /**
     * id
     * */
    protected int id;


    /**
     * 消息类型
     * */
    protected CommandCode commandCode;

    /**
     * 内容
     * */
    protected byte[] content = new byte[0];

    private Object object;
    private Throwable throwable;

    @Override
    public ProtocolCode getProtocolCode() {
        return ProtocolCode.fromBytes(ARPCProtocolV1.PROTOCOL_CODE);
    }

    @Override
    public byte getSerializer() {
        return 1;
    }

    @Override
    public ProtocolSwitch getProtocolSwitch() {
        return null;
    }

    @Override
    public void serialize() throws SerializationException {

    }

    @Override
    public void deserialize() throws DeserializationException {

    }

    @Override
    public void serializeContent(InvokeContext invokeContext) throws SerializationException {

    }

    @Override
    public void deserializeContent(InvokeContext invokeContext) throws DeserializationException {

    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public InvokeContext getInvokeContext() {
        return null;
    }

    @Override
    public CommandCode getCmdCode() {
        return commandCode;
    }

    public static RPCPackage createRequestMessage(RPCPackageCode commandCode) {
        RPCPackage message = new RPCPackage();
        message.setCommandCode(commandCode);
        return message;
    }
    public static RPCPackage createMessage(int id,RPCPackageCode commandCode) {
        RPCPackage message = new RPCPackage();
        message.setCommandCode(commandCode);
        message.setId(id);
        return message;
    }

    public static RPCPackage createHeardSynMessage() {
        return createRequestMessage(RPCPackageCode.HEART_SYN_COMMAND);
    }
    public static RPCPackage createHeardAckMessage() {
        return createRequestMessage(RPCPackageCode.HEART_ACK_COMMAND);
    }
}
