package com.nix.jingxun.addp.rpc.producer.netty;

import com.nix.jingxun.addp.rpc.common.config.CommandCode;
import com.nix.jingxun.addp.rpc.common.config.RPCProducerNettyConfig;
import com.nix.jingxun.addp.rpc.common.processor.HeartProcessor;
import com.nix.jingxun.addp.rpc.remoting.RemotingService;
import com.nix.jingxun.addp.rpc.remoting.netty.NettyRemotingServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author keray
 * @date 2018/12/07 19:45
 */
public class NettyServer {
    private HeartProcessor heartProcessor = new HeartProcessor();
    private RPCInvokeProcessor rpcInvokeProcessor = new RPCInvokeProcessor();
    private static RemotingService remotingService = new NettyRemotingServer(new RPCProducerNettyConfig());
    private final ThreadPoolExecutor heartExecutor = new ThreadPoolExecutor(1,1,60, TimeUnit.SECONDS,new LinkedBlockingQueue<>(), (ThreadFactory) Thread::new);
    private final ThreadPoolExecutor invokeExecutor = new ThreadPoolExecutor(32,32,1, TimeUnit.SECONDS,new LinkedBlockingQueue<>(), (ThreadFactory) Thread::new);
    private static boolean start = false;
    public synchronized void start() {
        if (start) {
            return;
        }
        remotingService = new NettyRemotingServer(new RPCProducerNettyConfig());
        start = true;
        remotingService.registerProcessor(CommandCode.HELLO.getCode(),heartProcessor,heartExecutor);
        remotingService.registerProcessor(CommandCode.SYNC_EXEC_METHOD.getCode(),rpcInvokeProcessor,invokeExecutor);
        remotingService.start();
    }
}
