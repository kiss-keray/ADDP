/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nix.jingxun.addp.rpc.remoting.protocol;

import com.alibaba.fastjson.annotation.JSONField;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import com.nix.jingxun.addp.rpc.remoting.annotation.CFNotNull;
import com.nix.jingxun.addp.rpc.remoting.exception.RemotingCommandException;
import com.nix.jingxun.addp.rpc.remoting.RemotingService;
import com.nix.jingxun.addp.rpc.remoting.netty.NettyDecoder;
import com.nix.jingxun.addp.rpc.remoting.CommandCustomHeader;
import com.nix.jingxun.addp.rpc.remoting.netty.NettyRequestProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jingxun.zds
 */
@Slf4j
public class RemotingCommand {
    public static final String SERIALIZE_TYPE_PROPERTY = "remoting.serialize.type";
    public static final String SERIALIZE_TYPE_ENV = "RRETOMING_SERIALIZE_TYPE";
    public static final String REMOTING_VERSION_KEY = "remoting.version";
    private static final Map<Class<? extends CommandCustomHeader>, Field[]> CLASS_HASH_MAP = new HashMap<>();
    private static final Map<Class, String> CANONICAL_NAME_CACHE = new HashMap<Class, String>();
    private static final Map<Field, Boolean> NULLABLE_FIELD_CACHE = new HashMap<>();
    private static final String STRING_CANONICAL_NAME = String.class.getCanonicalName();
    private static final String DOUBLE_CANONICAL_NAME_1 = Double.class.getCanonicalName();
    private static final String DOUBLE_CANONICAL_NAME_2 = double.class.getCanonicalName();
    private static final String INTEGER_CANONICAL_NAME_1 = Integer.class.getCanonicalName();
    private static final String INTEGER_CANONICAL_NAME_2 = int.class.getCanonicalName();
    private static final String LONG_CANONICAL_NAME_1 = Long.class.getCanonicalName();
    private static final String LONG_CANONICAL_NAME_2 = long.class.getCanonicalName();
    private static final String BOOLEAN_CANONICAL_NAME_1 = Boolean.class.getCanonicalName();
    private static final String BOOLEAN_CANONICAL_NAME_2 = boolean.class.getCanonicalName();
    private static volatile int configVersion = -1;
    private static AtomicInteger requestId = new AtomicInteger(0);
    private static SerializeType serializeTypeConfigInThisServer = SerializeType.JSON;
    static {
        final String protocol = System.getProperty(SERIALIZE_TYPE_PROPERTY, System.getenv(SERIALIZE_TYPE_ENV));
        if (!isBlank(protocol)) {
            try {
                serializeTypeConfigInThisServer = SerializeType.valueOf(protocol);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("parser specified protocol error. protocol=" + protocol, e);
            }
        }
    }

    /**
     * 命令-处理器模式下的
     * code作为命令到处理器的映射
     * key-processor
     * code 作为key
     * key-processor 由用户自己注册{@link RemotingService#registerProcessor(int, NettyRequestProcessor, ExecutorService)}
     * */
    private int code;
    /**
     * 消息版本
     * */
    private int version = 0;
    /**
     * 计数器计数
     * */
    private int opaque = requestId.getAndIncrement();
    /**
     * 标书数据包是request还是response
     * */
    private RemotingCommandType type = RemotingCommandType.REQUEST_COMMAND;
    private String remark;
    /**
     * 参数
     * */
    private HashMap<String, String> extFields;
    /**
     * 参数检验器
     * */
    private transient CommandCustomHeader customHeader;

    private SerializeType serializeTypeCurrentRPC = serializeTypeConfigInThisServer;

    /**
     * 数据
     * */
    private transient byte[] body;

    protected RemotingCommand() {
    }

    public static RemotingCommand createRequestCommand(int code, CommandCustomHeader customHeader) {
        RemotingCommand cmd = new RemotingCommand();
        cmd.setCode(code);
        cmd.customHeader = customHeader;
        setCmdVersion(cmd);
        return cmd;
    }

    private static void setCmdVersion(RemotingCommand cmd) {
        if (configVersion >= 0) {
            cmd.setVersion(configVersion);
        } else {
            String v = System.getProperty(REMOTING_VERSION_KEY);
            if (v != null) {
                int value = Integer.parseInt(v);
                cmd.setVersion(value);
                configVersion = value;
            }
        }
    }

    public static RemotingCommand createResponseCommand(Class<? extends CommandCustomHeader> classHeader) {
        return createResponseCommand(RemotingSysResponseCode.SYSTEM_ERROR, "not set any response code", classHeader);
    }

    public static RemotingCommand createResponseCommand(int code, String remark,
        Class<? extends CommandCustomHeader> classHeader) {
        RemotingCommand cmd = new RemotingCommand();
        cmd.markResponseType();
        cmd.setCode(code);
        cmd.setRemark(remark);
        setCmdVersion(cmd);

        if (classHeader != null) {
            try {
                CommandCustomHeader objectHeader = classHeader.newInstance();
                cmd.customHeader = objectHeader;
            } catch (InstantiationException e) {
                return null;
            } catch (IllegalAccessException e) {
                return null;
            }
        }

        return cmd;
    }

    public static RemotingCommand createResponseCommand(int code, String remark) {
        return createResponseCommand(code, remark, null);
    }

    public static RemotingCommand decode(final byte[] array) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        return decode(byteBuffer);
    }

    /**
     * 将字节数据解析为{@link RemotingCommand}
     * */
    public static RemotingCommand decode(final ByteBuffer byteBuffer) {
        int length = byteBuffer.limit();
        /**
         * 字节数据最前面32位整数保存数据包头长度
         * 第1个字节位 标识协议{@link #getProtocolType(int)} {@link #markProtocolType(int, SerializeType)}
         * 后面3个字节位标识头部长度 {@link #getHeaderLength(int)} {@link #markProtocolType(int, SerializeType)}
         */
        int oriHeaderLen = byteBuffer.getInt();
        int headerLength = getHeaderLength(oriHeaderLen);

        byte[] headerData = new byte[headerLength];
        byteBuffer.get(headerData);

        //解析数据包头部数据
        RemotingCommand cmd = headerDecode(headerData, getProtocolType(oriHeaderLen));

        //body长度总长度-首4位-头长度
        int bodyLength = length - 4 - headerLength;
        byte[] bodyData = null;
        if (bodyLength > 0) {
            bodyData = new byte[bodyLength];
            byteBuffer.get(bodyData);
        }
        cmd.body = bodyData;

        return cmd;
    }

    public static int getHeaderLength(int length) {
        return length & 0xFFFFFF;
    }

    private static RemotingCommand headerDecode(byte[] headerData, SerializeType type) {
        switch (type) {
            case JSON:
                RemotingCommand resultJson = RemotingSerializable.decode(headerData, RemotingCommand.class);
                resultJson.setSerializeTypeCurrentRPC(type);
                return resultJson;
            //未实现
            case XML:
                return null;
            default:
                break;
        }
        return null;
    }

    public static SerializeType getProtocolType(int source) {
        return SerializeType.valueOf((byte) ((source >> 24) & 0xFF));
    }

    public static int createNewRequestId() {
        return requestId.incrementAndGet();
    }

    public static SerializeType getSerializeTypeConfigInThisServer() {
        return serializeTypeConfigInThisServer;
    }

    private static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 设置数据包前4字节
     * @param source 头部长度
     * @param type 头部协议类型
     * 返回的4个byte
     * 第一个byte标识头部协议类型
     * 后三个byte标识头部长度 所以头部长度是0~2^24
     * */
    public static byte[] markProtocolType(int source, SerializeType type) {
        byte[] result = new byte[4];

        result[0] = type.getCode();
        result[1] = (byte) ((source >> 16) & 0xFF);
        result[2] = (byte) ((source >> 8) & 0xFF);
        result[3] = (byte) (source & 0xFF);
        return result;
    }

    public void markResponseType() {
        type = RemotingCommandType.RESPONSE_COMMAND;
    }

    public CommandCustomHeader readCustomHeader() {
        return customHeader;
    }

    public void writeCustomHeader(CommandCustomHeader customHeader) {
        this.customHeader = customHeader;
    }

    /**
     * 校验头部参数
     * */
    public CommandCustomHeader decodeCommandCustomHeader(
        Class<? extends CommandCustomHeader> classHeader) throws RemotingCommandException {
        CommandCustomHeader objectHeader;
        try {
            objectHeader = classHeader.newInstance();
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }

        if (this.extFields != null) {

            Field[] fields = getClazzFields(classHeader);
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    String fieldName = field.getName();
                    if (!fieldName.startsWith("this")) {
                        try {
                            String value = this.extFields.get(fieldName);
                            if (null == value) {
                                if (!isFieldNullable(field)) {
                                    throw new RemotingCommandException("the custom field <" + fieldName + "> is null");
                                }
                                continue;
                            }

                            field.setAccessible(true);
                            String type = getCanonicalName(field.getType());
                            Object valueParsed;

                            if (type.equals(STRING_CANONICAL_NAME)) {
                                valueParsed = value;
                            } else if (type.equals(INTEGER_CANONICAL_NAME_1) || type.equals(INTEGER_CANONICAL_NAME_2)) {
                                valueParsed = Integer.parseInt(value);
                            } else if (type.equals(LONG_CANONICAL_NAME_1) || type.equals(LONG_CANONICAL_NAME_2)) {
                                valueParsed = Long.parseLong(value);
                            } else if (type.equals(BOOLEAN_CANONICAL_NAME_1) || type.equals(BOOLEAN_CANONICAL_NAME_2)) {
                                valueParsed = Boolean.parseBoolean(value);
                            } else if (type.equals(DOUBLE_CANONICAL_NAME_1) || type.equals(DOUBLE_CANONICAL_NAME_2)) {
                                valueParsed = Double.parseDouble(value);
                            } else {
                                throw new RemotingCommandException("the custom field <" + fieldName + "> type is not supported");
                            }

                            field.set(objectHeader, valueParsed);

                        } catch (Throwable e) {
                            log.error("Failed field [{}] decoding", fieldName, e);
                        }
                    }
                }
            }

            objectHeader.checkFields();
        }

        return objectHeader;
    }

    /**
     * 做一个缓存  提高{@link Class#getDeclaredFields()} 效率
     * */
    private Field[] getClazzFields(Class<? extends CommandCustomHeader> classHeader) {
        Field[] field = CLASS_HASH_MAP.get(classHeader);

        if (field == null) {
            field = classHeader.getDeclaredFields();
            synchronized (CLASS_HASH_MAP) {
                CLASS_HASH_MAP.put(classHeader, field);
            }
        }
        return field;
    }

    /**
     * 判断 field 是否可以为空  缓存
     * */
    private boolean isFieldNullable(Field field) {
        if (!NULLABLE_FIELD_CACHE.containsKey(field)) {
            Annotation annotation = field.getAnnotation(CFNotNull.class);
            synchronized (NULLABLE_FIELD_CACHE) {
                NULLABLE_FIELD_CACHE.put(field, annotation == null);
            }
        }
        return NULLABLE_FIELD_CACHE.get(field);
    }

    private String getCanonicalName(Class clazz) {
        String name = CANONICAL_NAME_CACHE.get(clazz);

        if (name == null) {
            name = clazz.getCanonicalName();
            synchronized (CANONICAL_NAME_CACHE) {
                CANONICAL_NAME_CACHE.put(clazz, name);
            }
        }
        return name;
    }

    /**
     * 将自身编码为{@link ByteBuffer}
     * */
    public ByteBuffer encode() {
        int length = 4;
        byte[] headerData = this.headerEncode();
        length += headerData.length;
        if (this.body != null) {
            length += body.length;
        }
        // 最前面4位表示整个ByteBuffer长度
        ByteBuffer result = ByteBuffer.allocate(4 + length);
        result.putInt(length);
        result.put(markProtocolType(headerData.length, serializeTypeCurrentRPC));
        result.put(headerData);
        if (this.body != null) {
            result.put(this.body);
        }
        result.flip();
        return result;
    }

    private byte[] headerEncode() {
        this.makeCustomHeaderToNet();
        if (SerializeType.XML == serializeTypeCurrentRPC) {
            //未实现
            return RemotingSerializable.encode(this);
        } else {
            return RemotingSerializable.encode(this);
        }
    }

    public void makeCustomHeaderToNet() {
        if (this.customHeader != null) {
            Field[] fields = getClazzFields(customHeader.getClass());
            if (null == this.extFields) {
                this.extFields = new HashMap<String, String>();
            }

            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    String name = field.getName();
                    if (!name.startsWith("this")) {
                        Object value = null;
                        try {
                            field.setAccessible(true);
                            value = field.get(this.customHeader);
                        } catch (Exception e) {
                            log.error("Failed to access field [{}]", name, e);
                        }

                        if (value != null) {
                            this.extFields.put(name, value.toString());
                        }
                    }
                }
            }
        }
    }

    public ByteBuffer encodeHeader() {
        return encodeHeader(this.body != null ? this.body.length : 0);
    }

    public ByteBuffer encodeHeader(final int bodyLength) {
        /**
         * 4 表示ByteBuffer最前面4位保存数据包总长度
         * 由于{@link NettyDecoder} 继承{@link io.netty.handler.codec.LengthFieldBasedFrameDecoder}
         * 所以需要在数据包最前面加上数据包的总长度避免黏包问题
         */
        int length = 4;
        byte[] headerData;
        headerData = this.headerEncode();
        length += headerData.length;
        length += bodyLength;
        //这里的4 是因为保存header长度 作用于 result.put(markProtocolType(headerData.length, serializeTypeCurrentRPC));
        ByteBuffer result = ByteBuffer.allocate(4 + length - bodyLength);
        //在编码的ByteBuffer最前面加上数据包的总长度
        result.putInt(length);
        result.put(markProtocolType(headerData.length, serializeTypeCurrentRPC));
        result.put(headerData);
        result.flip();
        return result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
    public RemotingCommandType getType() {
        if (this.isResponseType()) {
            return RemotingCommandType.RESPONSE_COMMAND;
        }

        return RemotingCommandType.REQUEST_COMMAND;
    }

    public void setType(RemotingCommandType type) {
        this.type = type;
    }

    @JSONField(serialize = false)
    public boolean isResponseType() {
        return type == RemotingCommandType.RESPONSE_COMMAND;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getOpaque() {
        return opaque;
    }

    public void setOpaque(int opaque) {
        this.opaque = opaque;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public HashMap<String, String> getExtFields() {
        return extFields;
    }

    public void setExtFields(HashMap<String, String> extFields) {
        this.extFields = extFields;
    }

    public void addExtField(String key, String value) {
        if (null == extFields) {
            extFields = new HashMap<String, String>();
        }
        extFields.put(key, value);
    }

    @Override
    public String toString() {
        return "RemotingCommand [code=" + code + ", version=" + version + ", opaque=" + opaque + ", type="
            + type.name() + ", remark=" + remark + ", extFields=" + extFields + ", serializeTypeCurrentRPC="
            + serializeTypeCurrentRPC + "]";
    }

    public SerializeType getSerializeTypeCurrentRPC() {
        return serializeTypeCurrentRPC;
    }

    public void setSerializeTypeCurrentRPC(SerializeType serializeTypeCurrentRPC) {
        this.serializeTypeCurrentRPC = serializeTypeCurrentRPC;
    }
}