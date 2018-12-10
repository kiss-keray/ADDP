package com.nix.jingxun.addp.rpc.common.protocol;

import com.alipay.remoting.CommandCode;
import com.alipay.remoting.CommandHandler;
import com.alipay.remoting.RemotingContext;
import com.alipay.remoting.RemotingProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author keray
 * @date 2018/10/19 4:05 PM
 */
@Slf4j
public class ARPCCommandHandler implements CommandHandler {
    private final static ConcurrentHashMap<CommandCode, RemotingProcessor<RPCPackage>> PROCESSOR = new ConcurrentHashMap<>(16);
    private ExecutorService executorService;

    public ARPCCommandHandler() {
        RemotingProcessor remotingProcessor = new ResponseProcessor();
        this.registerProcessor(RPCPackageCode.RESPONSE_ERROR, remotingProcessor);
        this.registerProcessor(RPCPackageCode.RESPONSE_SUCCESS, remotingProcessor);
    }

    /**
     * Handle the command.
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void handleCommand(RemotingContext ctx, Object msg) throws Exception {
        if (msg instanceof List) {
            ((List) msg).forEach(message -> handler(ctx, message));
        } else {
            handler(ctx, msg);
        }
    }

    private void handler(RemotingContext ctx, Object msg) {
        if (msg instanceof RPCPackage) {
            try {
                PROCESSOR.get(((RPCPackage) msg).getCmdCode()).process(ctx, (RPCPackage) msg, executorService);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("处理message失败", e);
            }
        }
    }


    /**
     * Register processor for command with specified code.
     *
     * @param cmd
     * @param processor
     */
    @Override
    public void registerProcessor(CommandCode cmd, RemotingProcessor processor) {
        log.info("注册processor {} -> {}", cmd, processor.getClass().getName());
        PROCESSOR.putIfAbsent(cmd, processor);
    }

    /**
     * Register default executor for the handler.
     *
     * @param executor
     */
    @Override
    public void registerDefaultExecutor(ExecutorService executor) {
        executorService = executor;
    }

    /**
     * Get default executor for the handler.
     */
    @Override
    public ExecutorService getDefaultExecutor() {
        return executorService;
    }
}
