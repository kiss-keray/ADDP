package com.nix.jingxun.addp.rpc.common.config;

/**
 * @author keray
 * @date 2018/12/07 20:05
 */
public enum CommandCode {
    HELLO(0,"心跳测试包"),
    SYNC_EXEC_METHOD(1,"同步rpc调用");
    int code;
    String desc;

    CommandCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }}
