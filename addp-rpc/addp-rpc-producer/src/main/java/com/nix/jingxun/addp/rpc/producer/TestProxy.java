package com.nix.jingxun.addp.rpc.producer;
import com.nix.jingxun.addp.rpc.common.RPCResponse;
import com.nix.jingxun.addp.rpc.common.serializable.JsonSerializer;
import com.nix.jingxun.addp.rpc.common.serializable.Serializer;

/**
 * @author keray
 * @date 2018/12/07 22:43
 */
public class TestProxy {
    public static void main(String[] args) throws Exception {
        Serializer serializer = new JsonSerializer();
        RPCResponse response = new RPCResponse();
//        response.setCode(RPCResponse.ResponseCode.SUCCESS);
//        response.setResult(new RPCResponse.SuccessResult(User.class,new User("username",10,null)));
//        System.out.println(JSON.toJSONString(response));

        String json = "{\"code\":\"SUCCESS\",\"result\":{\"clazz\":\"com.nix.jingxun.addp.rpc.producer.User\"}}";
//        response = JSON.parseObject(json,RPCResponse.class);
        response = serializer.decoderResponse(json);
        System.out.println();
    }
}
