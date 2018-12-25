package com.nix.jingxun.addp.rpc.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author keray
 * @date 2018/12/09 14:09
 */
public final class RPCMethodParser {
    @AllArgsConstructor
    @Data
    public static class ServiceModel{
        private String interfaceName;
        private String appName;
        private String group;
        private String version;
        public String getKey() {
            return getMethodKey(this);
        }
    }

    public static String getMethodKey(ServiceModel model) {
        return String.format("%s-%s-%s-%s", model.getInterfaceName(), model.getAppName(), model.getGroup(), model.getVersion());
    }

    public static ServiceModel methodKey2Model(String key) {
        return new ServiceModel(key.split("-")[0],key.split("-")[1],key.split("-")[2],key.split("-")[3]);
    }
}
