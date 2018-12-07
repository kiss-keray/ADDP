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

import io.netty.channel.ChannelHandlerContext;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import com.nix.jingxun.addp.rpc.remoting.annotation.CFNullable;
import com.nix.jingxun.addp.rpc.remoting.exception.RemotingCommandException;
import com.nix.jingxun.addp.rpc.remoting.exception.RemotingConnectException;
import com.nix.jingxun.addp.rpc.remoting.exception.RemotingSendRequestException;
import com.nix.jingxun.addp.rpc.remoting.exception.RemotingTimeoutException;
import com.nix.jingxun.addp.rpc.remoting.exception.RemotingTooMuchRequestException;
import com.nix.jingxun.addp.rpc.remoting.netty.NettyClientConfig;
import com.nix.jingxun.addp.rpc.remoting.netty.NettyRemotingClient;
import com.nix.jingxun.addp.rpc.remoting.netty.NettyRemotingServer;
import com.nix.jingxun.addp.rpc.remoting.netty.NettyRequestProcessor;
import com.nix.jingxun.addp.rpc.remoting.netty.NettyServerConfig;
import com.nix.jingxun.addp.rpc.remoting.netty.ResponseFuture;
import com.nix.jingxun.addp.rpc.remoting.protocol.RemotingCommand;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class RemotingServerTest {
    private static RemotingServer remotingServer;
    private static RemotingClient remotingClient;

    public static RemotingServer createRemotingServer() throws InterruptedException {
        NettyServerConfig config = new NettyServerConfig();
        RemotingServer remotingServer = new NettyRemotingServer(config);
        remotingServer.registerProcessor(0, new NettyRequestProcessor() {
            @Override
            public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {
                request.setRemark("Hi " + ctx.channel().remoteAddress());
                return request;
            }

            @Override
            public boolean rejectRequest() {
                return false;
            }
        }, Executors.newCachedThreadPool());

        remotingServer.start();

        return remotingServer;
    }

    public static RemotingClient createRemotingClient() {
        return createRemotingClient(new NettyClientConfig());
    }

    public static RemotingClient createRemotingClient(NettyClientConfig nettyClientConfig) {
        RemotingClient client = new NettyRemotingClient(nettyClientConfig);
        client.start();
        return client;
    }

    @BeforeClass
    public static void setup() throws InterruptedException {
        remotingServer = createRemotingServer();
        remotingClient = createRemotingClient();
    }

    @AfterClass
    public static void destroy() {
        remotingClient.shutdown();
        remotingServer.shutdown();
    }

    @Test
    public void testInvokeSync() throws InterruptedException, RemotingConnectException,
        RemotingSendRequestException, RemotingTimeoutException {
        RequestHeader requestHeader = new RequestHeader();
        requestHeader.setCount(1);
        requestHeader.setMessageTitle("Welcome");
        RemotingCommand request = RemotingCommand.createRequestCommand(0, requestHeader);
        RemotingCommand response = remotingClient.invokeSync("localhost:8888", request, 1000 * 3);
        assertTrue(response != null);
        assertThat(response.getExtFields()).hasSize(2);

    }

    @Test
    public void testInvokeOneway() throws InterruptedException, RemotingConnectException,
        RemotingTimeoutException, RemotingTooMuchRequestException, RemotingSendRequestException {

        RemotingCommand request = RemotingCommand.createRequestCommand(0, null);
        request.setRemark("messi");
    }

    @Test
    public void testInvokeAsync() throws InterruptedException, RemotingConnectException,
        RemotingTimeoutException, RemotingTooMuchRequestException, RemotingSendRequestException {

        final CountDownLatch latch = new CountDownLatch(1);
        RemotingCommand request = RemotingCommand.createRequestCommand(0, null);
        request.setRemark("messi");
        remotingClient.invokeAsync("localhost:8888", request, 1000 * 3, new InvokeCallback() {
            @Override
            public void operationComplete(ResponseFuture responseFuture) {
                latch.countDown();
                assertTrue(responseFuture != null);
                assertThat(responseFuture.getResponseCommand().getExtFields()).hasSize(2);
            }
        });
        latch.await();
    }
}

class RequestHeader implements CommandCustomHeader {
    @CFNullable
    private Integer count;

    @CFNullable
    private String messageTitle;

    @Override
    public void checkFields() throws RemotingCommandException {
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }
}

