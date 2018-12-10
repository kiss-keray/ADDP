package com.nix.jingxun.addp.rpc.common;

/**
 * @author keray
 * @date 2018/12/09 14:09
 */
public final class RPCMethodParser {
    public static String getMethodKey(String interfaceName, String appName, String group, String version) {
        return String.format("%s-%s-%s-%s", interfaceName, appName, group, version);
    }
}
