package com.nix.jingxun.addp.rpc.common.serializable;

import com.nix.jingxun.addp.rpc.common.RPCRequest;

import java.io.Serializable;

/**
 * @author keray
 * @date 2018/12/08 13:11
 */
public interface Serializer {
    final String CLAZZ = "clazz";
    final String DATA = "data";
    RPCRequest decoderRequest(String requestStr) throws Exception;
    String encoderRequest(RPCRequest request);
}
