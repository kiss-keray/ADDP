package com.nix.jingxun.addp.rpc.common;


import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.InvokeCallbackListener;
import com.alipay.remoting.InvokeFuture;
import com.alipay.remoting.ResponseStatus;
import com.alipay.remoting.exception.ConnectionClosedException;
import com.alipay.remoting.rpc.exception.InvokeException;
import com.alipay.remoting.rpc.exception.InvokeServerBusyException;
import com.alipay.remoting.rpc.exception.InvokeServerException;
import com.alipay.remoting.rpc.exception.InvokeTimeoutException;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackage;
import com.nix.jingxun.addp.rpc.common.protocol.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionException;

/**
 * create by keray
 * date:2019/7/17 13:08
 */
@Slf4j
public class RpcInvokeCallbackListener implements InvokeCallbackListener {

    private String address;

    public RpcInvokeCallbackListener() {

    }

    public RpcInvokeCallbackListener(String address) {
        this.address = address;
    }


    /**
     * @see com.alipay.remoting.InvokeCallbackListener#onResponse(com.alipay.remoting.InvokeFuture)
     */
    @Override
    public void onResponse(InvokeFuture future) {
        InvokeCallback callback = future.getInvokeCallback();
        if (callback != null) {
            CallbackTask task = new CallbackTask(this.getRemoteAddress(), future);
            if (callback.getExecutor() != null) {
                // There is no need to switch classloader, because executor is provided by user.
                try {
                    callback.getExecutor().execute(task);
                } catch (RejectedExecutionException e) {
                    log.warn("Callback thread pool busy.");
                }
            } else {
                task.run();
            }
        }
    }

    class CallbackTask implements Runnable {

        InvokeFuture future;

        String remoteAddress;

        /**
         *
         */
        public CallbackTask(String remoteAddress, InvokeFuture future) {
            this.remoteAddress = remoteAddress;
            this.future = future;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            InvokeCallback callback = future.getInvokeCallback();
            // a lot of try-catches to protect thread pool
            RPCResponse response = null;
            try {
                RPCPackage rpcPackage = (RPCPackage) future.waitResponse(0);
                response = rpcPackage.coverResponse();
            } catch (InterruptedException e) {
                String msg = "Exception caught when getting response from InvokeFuture. The address is "
                        + this.remoteAddress;
                log.error(msg, e);
            }
            if (response == null || response.getStatus() != ResponseStatus.SUCCESS) {
                try {
                    Exception e;
                    if (response == null) {
                        e = new InvokeException("Exception caught in invocation. The address is "
                                + this.remoteAddress + " responseStatus:"
                                + ResponseStatus.UNKNOWN, future.getCause());
                    } else {
                        switch (response.getStatus()) {
                            case TIMEOUT:
                                e = new InvokeTimeoutException(
                                        "Invoke timeout when invoke with callback.The address is "
                                                + this.remoteAddress);
                                break;
                            case CONNECTION_CLOSED:
                                e = new ConnectionClosedException(
                                        "Connection closed when invoke with callback.The address is "
                                                + this.remoteAddress);
                                break;
                            case SERVER_THREADPOOL_BUSY:
                                e = new InvokeServerBusyException(
                                        "Server thread pool busy when invoke with callback.The address is "
                                                + this.remoteAddress);
                                break;
                            case SERVER_EXCEPTION:
                                String msg = "Server exception when invoke with callback.Please check the server log! The address is "
                                        + this.remoteAddress;
                                RPCResponse.ErrorResult ex = response.getError();
                                if (ex != null && ex.getException() != null) {
                                    e = new InvokeServerException(msg, ex.getException());
                                } else {
                                    e = new InvokeServerException(msg);
                                }
                                break;
                            default:
                                e = new InvokeException(
                                        "Exception caught in invocation. The address is "
                                                + this.remoteAddress + " responseStatus:"
                                                + response.getStatus(), future.getCause());

                        }
                    }
                    callback.onException(e);
                } catch (Throwable e) {
                    log.error(
                            "Exception occurred in user defined InvokeCallback#onException() logic, The address is {}",
                            this.remoteAddress, e);
                }
            } else {
                ClassLoader oldClassLoader = null;
                try {
                    if (future.getAppClassLoader() != null) {
                        oldClassLoader = Thread.currentThread().getContextClassLoader();
                        Thread.currentThread().setContextClassLoader(future.getAppClassLoader());
                    }
                    try {
                        if (response.getStatus() == ResponseStatus.SUCCESS) {
                            callback.onResponse(response.getResult() == null ? null : response.getResult().getData());
                        }
                    } catch (Throwable e) {
                        log.error("Exception occurred in user defined InvokeCallback#onResponse() logic.", e);
                    }
                } catch (Throwable e) {
                    log.error("Exception caught in RpcInvokeCallbackListener. The address is {}",
                            this.remoteAddress, e);
                } finally {
                    if (oldClassLoader != null) {
                        Thread.currentThread().setContextClassLoader(oldClassLoader);
                    }
                }
            } // enf of else
        } // end of run
    }

    /**
     * @see com.alipay.remoting.InvokeCallbackListener#getRemoteAddress()
     */
    @Override
    public String getRemoteAddress() {
        return this.address;
    }
}
