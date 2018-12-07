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

import com.nix.jingxun.addp.rpc.remoting.protocol.RemotingCommand;
import com.nix.jingxun.addp.rpc.remoting.netty.NettyRequestProcessor;

/**
 * @author jingxun.zds
 * {@link NettyRequestProcessor} processor处理{@link RemotingCommand} 前后处理钩子
 */
public interface RPCHook {
    /**
     * 处理器处理之前执行
     * @param remoteAddr
     * @param request
     * */
    void doBeforeRequest(final String remoteAddr, final RemotingCommand request);
    /**
     * 处理器执行之后执行
     * @param remoteAddr
     * @param request
     * @param response
     * */
    void doAfterResponse(final String remoteAddr, final RemotingCommand request,
        final RemotingCommand response);
}
