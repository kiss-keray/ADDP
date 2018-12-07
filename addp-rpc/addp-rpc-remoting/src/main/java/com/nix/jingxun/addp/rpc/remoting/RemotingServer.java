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

import com.nix.jingxun.addp.rpc.remoting.common.Pair;
import com.nix.jingxun.addp.rpc.remoting.exception.RemotingSendRequestException;
import com.nix.jingxun.addp.rpc.remoting.exception.RemotingTimeoutException;
import com.nix.jingxun.addp.rpc.remoting.exception.RemotingTooMuchRequestException;
import com.nix.jingxun.addp.rpc.remoting.netty.NettyRequestProcessor;
import com.nix.jingxun.addp.rpc.remoting.protocol.RemotingCommand;
import io.netty.channel.Channel;
import java.util.concurrent.ExecutorService;

public interface RemotingServer extends RemotingService {


    /**
     * 获取处理器
     * */
    Pair<NettyRequestProcessor, ExecutorService> getProcessorPair(final int requestCode);

    /**
     * 响应客户端同步等待
     * */
    RemotingCommand invokeSync(final Channel channel, final RemotingCommand request,
                               final long timeoutMillis) throws InterruptedException, RemotingSendRequestException,
            RemotingTimeoutException;

    /**
     * 响应客户端异步
     * @param invokeCallback 回调
     * */
    void invokeAsync(final Channel channel, final RemotingCommand request, final long timeoutMillis,
        final InvokeCallback invokeCallback) throws InterruptedException,
            RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;

}
