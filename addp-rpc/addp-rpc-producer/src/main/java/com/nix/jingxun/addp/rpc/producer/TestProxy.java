package com.nix.jingxun.addp.rpc.producer;

import com.alibaba.fastjson.JSON;
import com.nix.jingxun.addp.rpc.common.RPCRequest;
import com.nix.jingxun.addp.rpc.common.serializable.JsonSerializer;
import com.nix.jingxun.addp.rpc.common.serializable.Serializer;

import java.util.*;

/**
 * @author keray
 * @date 2018/12/07 22:43
 */
public class TestProxy {
    public static void main(String[] args) throws Exception {
//        new NettyServer().serverInit();

        String json = "{\n" +
                "    \"context\": {\n" +
                "        \"ip\": \"123\",\n" +
                "        \"h\": \"xsaxsa\"\n" +
                "    },\n" +
                "    \"date\": 123323224,\n" +
                "    \"interfaceName\": \"com.nix.xxxx\",\n" +
                "    \"method\": \"syaHello\",\n" +
                "    \"paramData\": [\n" +
                "        {\n" +
                "            \"clazz\": \"java.lang.String\",\n" +
                "            \"data\": \"hello world\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"clazz\": \"com.nix.jingxun.addp.rpc.producer.User\",\n" +
                "            \"data\": {\n" +
                "                \"username\": \"username\",\n" +
                "                \"age\": 22,\n" +
                "                \"child\": {\n" +
                "                    \"username\": \"username\",\n" +
                "                    \"age\": 22,\n" +
                "                    \"child\": {\n" +
                "                        \"username\": \"username\",\n" +
                "                        \"age\": 22\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"clazz\": \"java.lang.Integer\",\n" +
                "            \"data\": null\n" +
                "        },\n" +
                "        {\n" +
                "            \"clazz\": \"com.nix.jingxun.addp.rpc.common.config.CommandCode\",\n" +
                "            \"data\": \"SYNC_EXEC_METHOD\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"timeout\": 1000,\n" +
                "    \"type\": \"SYNC_EXEC_METHOD\",\n" +
                "    \"source\": {\n" +
                "        \"ip\": \"192.168.1.1\",\n" +
                "        \"appName\": \"app\"\n" +
                "    }\n" +
                "}";
        Serializer serializer = new JsonSerializer();
        RPCRequest request = serializer.decoderRequest(json);
        System.out.println();;
    }
}
