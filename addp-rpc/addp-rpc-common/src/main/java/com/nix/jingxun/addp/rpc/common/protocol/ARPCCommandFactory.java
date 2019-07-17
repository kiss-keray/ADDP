package com.nix.jingxun.addp.rpc.common.protocol;

import com.alipay.remoting.CommandFactory;
import com.alipay.remoting.RemotingCommand;
import com.alipay.remoting.ResponseStatus;
import com.alipay.remoting.exception.ConnectionClosedException;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeoutException;

/**
 * @author keray
 * @date 2018/10/19 4:25 PM
 */
public class ARPCCommandFactory implements CommandFactory {
    /**
     * create a request command with request object
     *
     * @param requestObject the request object included in request command
     * @return
     */
    @Override
    public RPCPackage createRequestCommand(Object requestObject) {
        return RPCPackage.builder()
                .object(requestObject)
                .build().nextId();
    }

    /**
     * create a normal response with response object
     *
     * @param responseObject
     * @param requestCmd
     * @return
     */
    @Override
    public RPCPackage createResponse(Object responseObject, RemotingCommand requestCmd) {
        return RPCPackage.builder()
                .id(requestCmd.getId())
                .object(RPCResponse.builder()
                        .status(ResponseStatus.SUCCESS)
                        .result(
                                RPCResponse.SuccessResult.builder()
                                        .clazz(responseObject.getClass())
                                        .data(responseObject)
                                        .build()
                        ))
                .build();
    }

    @Override
    public RPCPackage createExceptionResponse(int id, String errMsg) {
        return RPCPackage.builder()
                .id(id)
                .object(
                        RPCResponse.builder()
                                .error(
                                        RPCResponse.ErrorResult.builder()
                                                .errorMsg(errMsg)
                                                .build()
                                ).build()
                )
                .build();
    }

    @Override
    public RPCPackage createExceptionResponse(int id, Throwable t, String errMsg) {
        return RPCPackage.builder()
                .id(id)
                .object(
                        RPCResponse.builder()
                                .error(
                                        RPCResponse.ErrorResult.builder()
                                                .exception(t)
                                                .build()
                                )
                )
                .build();
    }

    @Override
    public RPCPackage createExceptionResponse(int id, ResponseStatus status) {
        return RPCPackage.builder()
                .id(id)
                .object(
                        RPCResponse.builder()
                                .status(status)
                                .build()
                )
                .build();
    }

    @Override
    public RPCPackage createExceptionResponse(int id, ResponseStatus status, Throwable t) {
        return RPCPackage.builder()
                .id(id)
                .object(
                        RPCResponse.builder()
                                .status(status)
                                .error(
                                        RPCResponse.ErrorResult.builder()
                                                .exception(t)
                                                .build()
                                )
                                .build()
                )
                .build();
    }

    @Override
    public RPCPackage createTimeoutResponse(InetSocketAddress address) {
        return RPCPackage.builder()
                .object(
                        RPCResponse.builder()
                                .status(ResponseStatus.TIMEOUT)
                                .error(RPCResponse.ErrorResult.builder()
                                        .exception(new TimeoutException(address.toString()))
                                        .build())
                                .build()
                )
                .build();
    }

    @Override
    public RPCPackage createSendFailedResponse(InetSocketAddress address, Throwable throwable) {
        return RPCPackage.builder()
                .object(
                        RPCResponse.builder()
                                .status(ResponseStatus.CLIENT_SEND_ERROR)
                                .error(RPCResponse.ErrorResult.builder()
                                        .exception(throwable)
                                        .build())
                                .build()
                )
                .build();
    }

    @Override
    public RPCPackage createConnectionClosedResponse(InetSocketAddress address, String message) {
        return RPCPackage.builder()
                .object(
                        RPCResponse.builder()
                                .status(ResponseStatus.CONNECTION_CLOSED)
                                .error(RPCResponse.ErrorResult.builder()
                                        .exception(new ConnectionClosedException(address.toString()))
                                        .errorMsg(message)
                                        .build())
                                .build()
                )
                .build();
    }
}
