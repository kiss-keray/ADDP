package com.nix.jingxun.addp.rpc.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author keray
 * @date 2018/12/08 15:57
 */
public final class RPCContext {
    private final static ConcurrentHashMap<String, String> RPC_CONTEXT = new ConcurrentHashMap<>(16);

    public static Map<String, String> getContext() {
        return RPC_CONTEXT;
    }

    public static boolean put(String key, String value) {
        return RPC_CONTEXT.put(key, value) == null;
    }

    public static void clear() {
        RPC_CONTEXT.clear();
    }

    public static void remove(String key) {
        RPC_CONTEXT.remove(key);
    }
}
