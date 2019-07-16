package com.nix.jingxun.addp.rpc.producer;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author keray
 * @date 2018/12/08 14:56
 */
@Slf4j
public final class InvokeContainer {

    private static final Map<String, RPCInvoke> INVOKE_INTERFACES = new HashMap<>(32);

    public static void addInterface(String key, RPCInvoke impl) {
        log.info("addInterface key={} clazz={} hashcode={}",key,impl.getClass(),impl.hashCode());
        INVOKE_INTERFACES.put(key, impl);
    }

    public static RPCInvoke getImpl(String key) {
        return INVOKE_INTERFACES.get(key);
    }

    public static boolean isExistImpl(String  key) {
        return INVOKE_INTERFACES.containsKey(key);
    }
}
