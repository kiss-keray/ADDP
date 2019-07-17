package com.nix.jingxun.addp.rpc.common.protocol;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alipay.remoting.CommandCode;
import com.alipay.remoting.InvokeContext;
import com.alipay.remoting.ProtocolCode;
import com.alipay.remoting.RemotingCommand;
import com.alipay.remoting.config.switches.ProtocolSwitch;
import com.alipay.remoting.exception.DeserializationException;
import com.alipay.remoting.exception.SerializationException;
import com.alipay.remoting.rpc.protocol.RpcResponseCommand;
import lombok.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author keray
 * @date 2018/12/09 00:17
 */
@Data
@Builder
public class RPCPackage implements RemotingCommand {
    private static final AtomicInteger NEXT_ID = new AtomicInteger(0);

    public RPCPackage() {
        this.id = NEXT_ID.getAndIncrement();
    }

    public RPCPackage(int id, CommandCode commandCode, byte[] content, Object object, PackageObject jsonObject) {
        this.id = id;
        this.commandCode = commandCode;
        this.content = content;
        this.object = object;
        this.jsonObject = jsonObject;
    }

    /**
     * id
     */
    protected int id;


    /**
     * 消息类型
     */
    protected CommandCode commandCode;

    /**
     * 内容
     */
    @JSONField(serialize = false)
    protected byte[] content = new byte[0];


    private Object object;


    private PackageObject jsonObject;


    public RPCPackage nextId() {
        this.id = NEXT_ID.getAndIncrement();
        return this;
    }

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
        if (getSerializer() == 1 && this.object != null) {
            this.jsonObject = new PackageObject(this.object.getClass(), this.object);
            setContent(JSON.toJSONString(jsonObject).getBytes());
        }
    }

    @Override
    public void deserialize() throws DeserializationException {
        if (getSerializer() == 1 && this.getContent() != null && this.getContent().length > 0) {
            this.jsonObject = JSON.parseObject(new String(getContent()), PackageObject.class);
            this.object = JSON.parseObject(JSON.toJSONString(jsonObject.getData()), jsonObject.getClazz());
        }
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

    public void setObject(Object object) {
        this.object = object;
    }

    public RPCRequest coverRequest() {
        return (RPCRequest) object;
    }
    public RPCResponse coverResponse() {
        RPCResponse response = (RPCResponse) object;
        if (response.getResult() != null) {
            response.getResult().setData(JSON.parseObject(JSON.toJSONString(response.getResult().getData()), response.getResult().getClazz()));
        }
        return response;
    }
    public static RPCPackage createRequestMessage(RPCPackageCode commandCode) {
        RPCPackage message = new RPCPackage();
        message.setCommandCode(commandCode);
        return message;
    }

    public static RPCPackage createMessage(int id, RPCPackageCode commandCode) {
        RPCPackage message = new RPCPackage();
        message.setCommandCode(commandCode);
        message.setId(id);
        return message;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class PackageObject {
        private Class<?> clazz;
        private Object data;
    }

    public static RPCPackage createHeardSynMessage() {
        return createRequestMessage(RPCPackageCode.HEART_SYN_COMMAND);
    }

    public static RPCPackage createHeardAckMessage() {
        return createRequestMessage(RPCPackageCode.HEART_ACK_COMMAND);
    }

    @Override
    public String toString() {
        return "RPCPackage{" +
                "id=" + id +
                ", commandCode=" + commandCode +
                ", object=" + object +
                ", jsonObject=" + jsonObject +
                '}';
    }
}
