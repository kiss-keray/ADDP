package com.nix.jingxun.addp.rpc.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.nix.jingxun.addp.rpc.common.config.CommandCode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
{
    "context": {
        "ip": "123",
        "h": "xsaxsa"
    },
    "date": 123323224,
    "interfaceName": "com.nix.xxxx",
    "method": "syaHello",
    "data": [
        {
            "clazz": "java.lang.String",
            "data": "hello world"
        },
        {
            "clazz": "com.nix.jingxun.addp.rpc.producer.User",
            "data": {
                "username": "username",
                "age": 22,
                "child": {
                    "username": "username",
                    "age": 22,
                    "child": {
                        "username": "username",
                        "age": 22
                    }
                }
            }
        },
        {
            "clazz": "java.lang.Integer",
            "data": null
        },
        {
            "clazz": "com.nix.jingxun.addp.rpc.common.config.CommandCode",
            "data": "SYNC_EXEC_METHOD"
        }
    ],
    "timeout": 1000,
    "type": "SYNC_EXEC_METHOD",
    "source": {
        "ip": "192.168.1.1",
        "appName": "app"
    }
}
 *
 * @author jingxun.zds*/
@Data
@AllArgsConstructor
public class RPCRequest implements Serializable {
    private String interfaceName;
    private String method;
    private CommandCode type;
    private long timeout;
    private Map<String,Object> context;
    private Date date;
    private List<ParamsData> paramData;
    private Source source;
    public Class[] getMethodParamTypes() {
        return getParamData().stream().map(ParamsData::getClazz).toArray(Class[]::new);
    }
    @Data
    @AllArgsConstructor
    public static class Source{
        private String ip;
        private String appName;

    }
    @Data
    @AllArgsConstructor
    public static class ParamsData{
        private Class clazz;
        private Object data;

        public void setClazz(String clazz) {
            try {
                this.clazz = Class.forName(clazz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

