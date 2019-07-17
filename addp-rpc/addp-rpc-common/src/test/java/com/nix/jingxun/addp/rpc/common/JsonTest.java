package com.nix.jingxun.addp.rpc.common;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

/**
 * @author keray
 * @date 2018/12/09 13:55
 */
public class JsonTest {

    @Test
    public void producerRequestTest() {
        String json = "{\n" +
                "    \"appName\": \"rpc-web\",\n" +
                "    \"group\": \"RPC\",\n" +
                "    \"host\": \"127.0.0.1:15000\",\n" +
                "    \"interfaceName\": \"com.nix.xxx.Hello\",\n" +
                "    \"methods\": [\n" +
                "        {\n" +
                "            \"methodName\": \"sayHello\",\n" +
                "            \"paramType\": [\n" +
                "                \"java.lang.String\",\n" +
                "                \"com.nix.jingxun.addp.rpc.common.protocol.RPCRequest\"\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"version\": \"1.0.0\"\n" +
                "}";
        Producer2ServerRequest producer2SercerRequest = JSON.parseObject(json, Producer2ServerRequest.class);
        System.out.println();
    }
}
