package com.nix.jingxun.addp.rpc.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.nix.jingxun.addp.rpc.common.config.CommandCode;

import java.util.*;

/**
 * @author keray
 * @date 2018/12/07 22:43
 */
public class TestProxy {
    public static void main(String[] args) throws Exception {
//        new NettyServer().serverInit();

        String json = "{\n" +
                "    \"interfaceName\": \"com.nix.xxxx\",\n" +
                "    \"method\": \"syaHello\",\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"clazz\": \"java.lang.String\",\n" +
                "            \"data\": \"hello world\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"clazz\": \"com.nix.jingxun.addp.rpc.producer.User\",\n" +
                "            \"data\": {\n" +
                "                \"username\": \"username\",\n" +
                "                \"age\": 22\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"clazz\": \"java.lang.Integer\",\n" +
                "            \"data\": null\n" +
                "        }\n" +
                "    ],\n" +
                "    \"type\": \"SYNC_EXEC_METHOD\",\n" +
                "    \"timeout\": 1000,\n" +
                "    \"context\": {\n" +
                "        \"ip\": \"123\",\n" +
                "        \"h\": \"xsaxsa\"\n" +
                "    },\n" +
                "    \"source\": {\n" +
                "        \"ip\": \"xx.xx.xx.xx\",\n" +
                "        \"app\": \"appName\"\n" +
                "    },\n" +
                "    \"date\": 123323224\n" +
                "}";

        RPCRequest request = JSON.parseObject(json,RPCRequest.class);
        request.setParamData(decoderData(JSON.parseArray("[\n" +
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
                "                    \"clazz\": \"com.nix.jingxun.addp.rpc.producer.User\",\n" +
                "                    \"data\": {\n" +
                "                        \"username\": \"username\",\n" +
                "                        \"age\": 22,\n" +
                "                        \"child\": {\n" +
                "                            \"clazz\": \"com.nix.jingxun.addp.rpc.producer.User\",\n" +
                "                            \"data\": {\n" +
                "                                \"username\": \"username\",\n" +
                "                                \"age\": 22\n" +
                "                            }\n" +
                "                        }\n" +
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
                "    ]")));
        System.out.println(Arrays.toString(request.getMethodParamTypes()));
    }

    private static List<RPCRequest.ParamsData> decoderData(List jsonArray) throws Exception {
        List<RPCRequest.ParamsData> paramData = new ArrayList<>(32);
        for (Object d:jsonArray) {
            Map<String,String> d1 =  ((Map<String,String>)d);
            paramData.add(new RPCRequest.ParamsData(Class.forName(d1.get("clazz")),de(d1)));
        }
        return paramData;
    }
    private static Object de(Object data) throws Exception {
        if (data instanceof JSONObject) {
            if (((JSONObject) data).containsKey("clazz")) {
                try {
                    if (null == ((JSONObject) data).get("data")) {
                        return null;
                    }
                    Object result = JSON.parseObject(((JSONObject) data).get("data").toString(), Class.forName(((JSONObject) data).get("clazz").toString()));
                    if (result instanceof JSONArray) {
                        List child = new ArrayList(32);
                        for (Object o:(JSONArray)result) {
                            child.add(de(data));
                        }
                        return child;
                    } else {
                        return de(result);
                    }
                }catch (JSONException e) {
                    if (char.class.getName().equals(((JSONObject) data).get("clazz"))) {
                        return ((JSONObject) data).getString("data").charAt(0);
                    }
                    if (String.class.getName().equals(((JSONObject) data).get("clazz"))) {
                        return ((JSONObject) data).get("data");
                    }
//                    if (Class.forName(String.valueOf(((JSONObject) data).get("clazz"))).isEnum()) {
//                        return Class.forName(String.valueOf(((JSONObject) data).get("clazz"))).getMethod("name",String.class).invoke(null,((JSONObject) data).get("data"));
//                    }
                    return Class.forName((String) ((JSONObject) data).get("clazz")).getMethod("valueOf",String.class).invoke(null,((JSONObject) data).get("data"));
                }
            }
        }
        return data;
    }
}
