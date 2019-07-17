package com.nix.jingxun.addp.rpc.common.protocol;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {
 * "context": {
 * "ip": "123",
 * "h": "xsaxsa"
 * },
 * "date": 123323224,
 * "interfaceName": "com.nix.xxxx",
 * "method": "syaHello",
 * "paramData": [
 * {
 * "clazz": "java.lang.String",
 * "data": "hello world"
 * },
 * {
 * "clazz": "com.nix.jingxun.addp.rpc.producer.User",
 * "data": {
 * "username": "username",
 * "age": 22,
 * "child": {
 * "username": "username",
 * "age": 22,
 * "child": {
 * "username": "username",
 * "age": 22
 * }
 * }
 * }
 * },
 * {
 * "clazz": "java.lang.Integer",
 * "data": null
 * },
 * {
 * "clazz": "com.nix.jingxun.addp.rpc.common.config.CommandCode",
 * "data": "SYNC_EXEC_METHOD"
 * }
 * ],
 * "timeout": 1000,
 * "type": "SYNC_EXEC_METHOD",
 * "source": {
 * "ip": "192.168.1.1",
 * "appName": "app"
 * }
 * }
 *
 * @author jingxun.zds
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RPCRequest implements Serializable {
    private String interfaceName;
    private String method;
    private long timeout;
    private Map<String, String> context;
    private Date date;
    private ParamsData[] paramData;
    private Source source;
    private String[] methodParamTypes;

    public Class[] getMethodParamTypes() {
        if (methodParamTypes == null) {
            return null;
        }
        return Stream.of(methodParamTypes).map(item -> {
            try {
                return Class.forName(item);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }).toArray(Class[]::new);
    }

    public Object[] getParams() {
        if (paramData == null) {
            return null;
        }
        return Stream.of(paramData).map(ParamsData::getData).toArray();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Source {
        private String ip;
        private String appName;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class ParamsData {
        private String clazz;
        private Object data;
    }
}

