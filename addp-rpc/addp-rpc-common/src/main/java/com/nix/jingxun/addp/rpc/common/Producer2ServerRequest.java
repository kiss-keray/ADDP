package com.nix.jingxun.addp.rpc.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author keray
 * @date 2018/12/09 13:49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Producer2ServerRequest {
    private String host;
    private String interfaceName;
    private String appName;
    private String group;
    private String version;
    private MethodMsg[] methods;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class MethodMsg {
        private String methodName;
        private String[] paramType;
        private String returnType;
    }
}
