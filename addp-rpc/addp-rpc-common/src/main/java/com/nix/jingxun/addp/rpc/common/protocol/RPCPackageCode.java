package com.nix.jingxun.addp.rpc.common.protocol;

import com.alipay.remoting.CommandCode;

/**
 * @author keray
 * @date 2018/12/09 00:33
 */
public enum RPCPackageCode implements CommandCode {
    RESPONSE_SUCCESS((short) 0xa0, "请求响应包OK"),
    RESPONSE_ERROR((short) 0xa1, "请求响应包ERROR"),
    HEART_SYN_COMMAND((short) 0x07, "请求心跳数据包"),
    HEART_ACK_COMMAND((short) 0x08, "确认心跳数据包"),
    RPC_INVOKE((short) 0x10, "RPC调用"),
    PRODUCER_REGISTER((short) 0x12, "服务方注册服务"),
    CONSUMER_GET_MSG((short) 0x13, "消费者获取接口信息"),
    PRODUCER_RECON((short) 0x14, "服务方重连中心");

    short code;

    String desc;

    /**
     * the short value of the code
     */
    RPCPackageCode(short code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * @return the short value of the code
     */
    @Override
    public short value() {
        return code;
    }

    public static RPCPackageCode valueOfCode(short code) {
        for (RPCPackageCode e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }
}
