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

package com.nix.jingxun.addp.rpc.remoting.netty;

/**
 * @author jingxun.zds
 * 系统配置
 */
public class NettySystemConfig {
    public static final String COM_ROCKETMQ_REMOTING_SOCKET_SNDBUF_SIZE = "com.nix.remoting.socket.sndbuf.size";
    public static final String COM_ROCKETMQ_REMOTING_SOCKET_RCVBUF_SIZE = "com.nix.remoting.socket.rcvbuf.size";
    public static final String COM_ROCKETMQ_REMOTING_CLIENT_ASYNC_SEMAPHORE_VALUE = "com.nix.remoting.clientAsyncSemaphoreValue";

    public static final int CLIENT_ASYNC_SEMAPHORE_VALUE = Integer.parseInt(System.getProperty(COM_ROCKETMQ_REMOTING_CLIENT_ASYNC_SEMAPHORE_VALUE, "65535"));
    public static int socketSndbufSize = Integer.parseInt(System.getProperty(COM_ROCKETMQ_REMOTING_SOCKET_SNDBUF_SIZE, "65535"));
    public static int socketRcvbufSize = Integer.parseInt(System.getProperty(COM_ROCKETMQ_REMOTING_SOCKET_RCVBUF_SIZE, "65535"));
}
