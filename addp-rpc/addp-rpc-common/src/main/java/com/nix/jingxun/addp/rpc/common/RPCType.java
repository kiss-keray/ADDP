package com.nix.jingxun.addp.rpc.common;

/**
 * @author keray
 * @date 2018/12/09 01:13
 */
public enum RPCType {

    SYNC_EXEC_METHOD(1, "同步rpc调用"),
    ASYNC_EXEC_METHOD(2, "异步rpc调用");
    int code;
    String desc;

    RPCType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
