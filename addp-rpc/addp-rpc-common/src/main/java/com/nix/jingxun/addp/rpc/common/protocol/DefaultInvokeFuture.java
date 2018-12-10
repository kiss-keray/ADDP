package com.nix.jingxun.addp.rpc.common.protocol;

import com.alipay.remoting.*;
import io.netty.util.Timeout;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author keray
 * @date 2018/11/07 上午10:15
 */
@Slf4j
public class DefaultInvokeFuture implements InvokeFuture {
    private int invokeId;

    private InvokeCallbackListener callbackListener;

    private InvokeCallback callback;

    private volatile RPCPackage response;

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    private final AtomicBoolean executeCallbackOnlyOnce = new AtomicBoolean(false);

    private Timeout timeout;

    private Throwable cause;

    private ClassLoader classLoader;

    private byte protocol;

    private InvokeContext invokeContext;

    private CommandFactory commandFactory;

    public DefaultInvokeFuture(int invokeId, InvokeCallbackListener callbackListener,
                               InvokeCallback callback, byte protocol, CommandFactory commandFactory) {
        this.invokeId = invokeId;
        this.callbackListener = callbackListener;
        this.callback = callback;
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.protocol = protocol;
        this.commandFactory = commandFactory;
    }

    public DefaultInvokeFuture(int invokeId, InvokeCallbackListener callbackListener,
                               InvokeCallback callback, byte protocol,
                               CommandFactory commandFactory, InvokeContext invokeContext) {
        this(invokeId, callbackListener, callback, protocol, commandFactory);
        this.invokeContext = invokeContext;
    }

    @Override
    public RemotingCommand waitResponse(long timeoutMillis) throws InterruptedException {
        this.countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
        return this.response;
    }

    @Override
    public RemotingCommand waitResponse() throws InterruptedException {
        this.countDownLatch.await();
        return this.response;
    }

    @Override
    public RemotingCommand createConnectionClosedResponse(InetSocketAddress responseHost) {
        return this.commandFactory.createConnectionClosedResponse(responseHost, null);
    }

    @Override
    public void putResponse(RemotingCommand remotingCommand) {
        this.response = (RPCPackage) remotingCommand;
        countDownLatch.countDown();
    }

    @Override
    public int invokeId() {
        return this.invokeId;
    }

    @Override
    public void executeInvokeCallback() {
        if (callbackListener != null) {
            if (this.executeCallbackOnlyOnce.compareAndSet(false, true)) {
                callbackListener.onResponse(this);
            }
        }
    }

    @Override
    public void tryAsyncExecuteInvokeCallbackAbnormally() {
        try {
            Protocol protocol = ProtocolManager.getProtocol(ProtocolCode.fromBytes(this.protocol));
            if (null != protocol) {
                CommandHandler commandHandler = protocol.getCommandHandler();
                if (null != commandHandler) {
                    ExecutorService executor = commandHandler.getDefaultExecutor();
                    if (null != executor) {
                        executor.execute(() -> {
                            ClassLoader oldClassLoader = null;
                            try {
                                if (DefaultInvokeFuture.this.getAppClassLoader() != null) {
                                    oldClassLoader = Thread.currentThread()
                                            .getContextClassLoader();
                                    Thread.currentThread().setContextClassLoader(
                                            DefaultInvokeFuture.this.getAppClassLoader());
                                }
                                DefaultInvokeFuture.this.executeInvokeCallback();
                            } finally {
                                if (null != oldClassLoader) {
                                    Thread.currentThread()
                                            .setContextClassLoader(oldClassLoader);
                                }
                            }
                        });
                    }
                } else {
                    log.error("Executor null in commandHandler of protocolCode [{}].",
                            this.protocol);
                }
            } else {
                log.error("protocolCode [{}] not registered!", this.protocol);
            }
        } catch (Exception e) {
            log.error("Exception caught when executing invoke callback abnormally.", e);
        }
    }

    @Override
    public void setCause(Throwable throwable) {
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }

    @Override
    public InvokeCallback getInvokeCallback() {
        return this.callback;
    }

    @Override
    public void addTimeout(Timeout timeout) {
        this.timeout = timeout;
    }

    @Override
    public void cancelTimeout() {
        if (this.timeout != null) {
            this.timeout.cancel();
        }
    }

    @Override
    public boolean isDone() {
        return countDownLatch.getCount() <= 0;
    }

    @Override
    public ClassLoader getAppClassLoader() {
        return this.classLoader;
    }

    @Override
    public byte getProtocolCode() {
        return this.protocol;
    }

    @Override
    public void setInvokeContext(InvokeContext invokeContext) {
        this.invokeContext = invokeContext;
    }

    @Override
    public InvokeContext getInvokeContext() {
        return invokeContext;
    }
}
