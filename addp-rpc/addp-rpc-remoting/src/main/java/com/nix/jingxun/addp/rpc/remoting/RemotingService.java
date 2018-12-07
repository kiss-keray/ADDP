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

package com.nix.jingxun.addp.rpc.remoting;


import com.nix.jingxun.addp.rpc.remoting.netty.NettyRequestProcessor;

import java.util.concurrent.ExecutorService;

/**
 * @author jingxun.zds
 * 远程服务超级接口
 */
public interface RemotingService {
    /**
     * 服务启动
     * */
    void start();

    /**
     * 关闭服务
     * */
    void shutdown();
    /**
     * 注册RPCHook
     * @param rpcHook
     * */
    void registerRPCHook(RPCHook rpcHook);

    /**
     * 注册消息处理器
     * @param requestCode 处理器映射key
     * @param processor 处理器
     * @param executor 执行器
     * */
    void registerProcessor(final int requestCode, final NettyRequestProcessor processor,
                           final ExecutorService executor);
}
