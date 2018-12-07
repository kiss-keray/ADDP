package com.nix.jingxun.addp.rpc.common.client;

import com.nix.jingxun.addp.rpc.remoting.RemotingClient;
import com.nix.jingxun.addp.rpc.remoting.exception.RemotingConnectException;
import com.nix.jingxun.addp.rpc.remoting.exception.RemotingSendRequestException;
import com.nix.jingxun.addp.rpc.remoting.exception.RemotingTimeoutException;
import com.nix.jingxun.addp.rpc.remoting.exception.RemotingTooMuchRequestException;
import com.nix.jingxun.addp.rpc.remoting.netty.NettyClientConfig;
import com.nix.jingxun.addp.rpc.remoting.netty.NettyRemotingClient;
import com.nix.jingxun.addp.rpc.remoting.netty.ResponseFuture;
import com.nix.jingxun.addp.rpc.remoting.protocol.RemotingCommand;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * @author keray
 * @date 2018/12/07 18:56
 */
@Slf4j
public class NettyClient {
    private final static String RPC_HOST = "rpc.xx11.top";
    private final static RemotingClient REMOTING_CLIENT = new NettyRemotingClient(new NettyClientConfig());
    static {
        REMOTING_CLIENT.start();
    }
    public static RemotingClient getRemotingClient() {
        return REMOTING_CLIENT;
    }
    public static ResponseFuture invokeAsync(RemotingCommand command) {
        return invokeAsync(RPC_HOST,command);
    }
    public static ResponseFuture invokeAsync(final String host,RemotingCommand command) {
        try {
            return REMOTING_CLIENT.invokeAsync(host,command,0);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("netty client invokeAsync fail.{}",command);
        }
        return null;
    }
    public static void oneway(RemotingCommand command) throws Exception{
        oneway(RPC_HOST,command);
    }
    public static void oneway(final String host,RemotingCommand command) throws Exception{
        try {
            REMOTING_CLIENT.oneway(host,command);
        } catch (Exception e) {
            throw  e;
        }
    }


    public static RemotingCommand invokeSync(final RemotingCommand command,long timeout) throws RemotingTimeoutException {
        return invokeSync(RPC_HOST,command,timeout);
    }

    public static RemotingCommand invokeSync(final String host,final RemotingCommand command,long timeout) throws RemotingTimeoutException {
        try {
            return REMOTING_CLIENT.invokeSync(host,command,timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RemotingConnectException e) {
            e.printStackTrace();
        } catch (RemotingSendRequestException e) {
            e.printStackTrace();
        } catch (RemotingTooMuchRequestException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void invokeAsync(RemotingCommand command, Consumer<ResponseFuture> consumer) throws RemotingTimeoutException {
        invokeAsync(RPC_HOST,command,consumer);
    }

    public static void invokeAsync(final String host,RemotingCommand command, Consumer<ResponseFuture> consumer) throws RemotingTimeoutException {
        try {
            REMOTING_CLIENT.invokeAsync(host,command,0,consumer::accept);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RemotingConnectException e) {
            e.printStackTrace();
        } catch (RemotingTooMuchRequestException e) {
            e.printStackTrace();
        } catch (RemotingSendRequestException e) {
            e.printStackTrace();
        }
    }
}
