package com.nix.jingxun.addp.rpc.producer;

import com.alibaba.fastjson.annotation.JSONField;
import com.nix.jingxun.addp.rpc.common.config.CommandCode;
import com.sun.org.apache.regexp.internal.RE;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author keray
 * @date 2018/12/07 23:13
 */
/**
 {
 "interfaceName":"com.nix.xxxx",
 "method":"syaHello",
 "data":[{"clazz":"java.lang.String","data":"hello world"},{"clazz":"com.nix.xxx","data":{"name":"name","password":"***"}},"java.lang.Integer":null],
 "type":"SYNC_EXEC_METHOD" -- CommandCode.SYNC_EXEC_METHOD,
 "timeout":1000,
 "context":{} -- map,
 "source":{"ip":"xx.xx.xx.xx","app":"appName"},
 "date":123323224
 }
 *
 * */
@Data
@AllArgsConstructor
public class RPCRequest implements Serializable {
    String interfaceName;
    String method;
    CommandCode type;
    long timeout;
    Map<String,Object> context;
    Date date;
    @JSONField(serialize = false)
    List<ParamsData> paramData;
    public Class[] getMethodParamTypes() {
        return getParamData().stream().map(ParamsData::getClazz).collect(Collectors.toList()).toArray(new Class[0]);
    }
    @Data
    @AllArgsConstructor
    static class Source{
        private String ip;
        private String appName;

    }
    @Data
    @AllArgsConstructor
    static class ParamsData{
        private Class clazz;
        private Object data;
    }
}

