package com.nix.jingxun.addp.rpc.producer;
import com.nix.jingxun.addp.rpc.common.RPCRequest;
import com.nix.jingxun.addp.rpc.common.RPCResponse;
import com.nix.jingxun.addp.rpc.common.serializable.JsonSerializer;
import com.nix.jingxun.addp.rpc.common.serializable.Serializer;

/**
 * @author keray
 * @date 2018/12/07 22:43
 */
public class TestProxy {
    public static void main(String[] args) throws Exception {
        RPCRequest request = new RPCRequest();
        System.out.println(request.getMethodParamTypes() == null);
    }
}
