package com.nix.jingxun.addp.rpc.common.serializable;

import com.nix.jingxun.addp.rpc.common.RPCRequest;
import com.nix.jingxun.addp.rpc.common.RPCResponse;
import org.springframework.stereotype.Component;

/**
 * @author keray
 * @date 2018/12/08 13:11
 */
@Component
public interface Serializer {
    RPCRequest decoderRequest(String requestStr) throws Exception;
    String encoderRequest(RPCRequest request) throws Exception;

    RPCResponse decoderResponse(String responseStr) throws Exception;
    String encoderResponse(RPCResponse Response) throws Exception;
}
