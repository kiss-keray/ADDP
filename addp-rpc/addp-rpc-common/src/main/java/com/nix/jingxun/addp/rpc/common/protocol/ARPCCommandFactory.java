package com.nix.jingxun.addp.rpc.common.protocol;

import com.alipay.remoting.CommandFactory;
import com.alipay.remoting.RemotingCommand;
import com.alipay.remoting.ResponseStatus;

import java.net.InetSocketAddress;

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
        return null;
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
        return null;
    }

    @Override
    public RPCPackage createExceptionResponse(int id, String errMsg) {
        RPCPackage rpcPackage = RPCPackage.createMessage(id,RPCPackageCode.RESPONSE_ERROR);
        rpcPackage.setContent(errMsg.getBytes());
        return rpcPackage;
    }

    @Override
    public RPCPackage createExceptionResponse(int id, Throwable t, String errMsg) {
        return null;
    }

    @Override
    public RPCPackage createExceptionResponse(int id, ResponseStatus status) {
        return null;
    }

    @Override
    public RPCPackage createExceptionResponse(int id, ResponseStatus status, Throwable t) {
        return null;
    }

    @Override
    public RPCPackage createTimeoutResponse(InetSocketAddress address) {
        return null;
    }

    @Override
    public RPCPackage createSendFailedResponse(InetSocketAddress address, Throwable throwable) {
        return null;
    }

    @Override
    public RPCPackage createConnectionClosedResponse(InetSocketAddress address, String message) {
        return null;
    }
}
