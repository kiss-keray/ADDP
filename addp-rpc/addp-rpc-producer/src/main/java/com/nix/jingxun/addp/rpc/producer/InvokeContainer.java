package com.nix.jingxun.addp.rpc.producer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author keray
 * @date 2018/12/08 14:56
 */
public final class InvokeContainer {

    private static final Map<String, Object> INVOKE_INTERFACES = new HashMap<>(32);

    public static void addInterface(String key, Object impl) {
        INVOKE_INTERFACES.put(key, impl);
    }

    public static Object getImpl(String key) {
        return INVOKE_INTERFACES.get(key);
    }
}
