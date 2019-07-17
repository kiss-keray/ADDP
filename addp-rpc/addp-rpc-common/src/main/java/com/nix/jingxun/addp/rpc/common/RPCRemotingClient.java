package com.nix.jingxun.addp.rpc.common;

import com.alipay.remoting.*;
import com.alipay.remoting.config.ConfigurableInstance;
import com.alipay.remoting.config.switches.GlobalSwitch;
import com.alipay.remoting.connection.ConnectionFactory;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.HeartbeatHandler;
import com.alipay.remoting.util.RemotingUtil;
import com.nix.jingxun.addp.rpc.common.protocol.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Kiss
 * @date 2018/10/21 0:36
 */
@Slf4j
public class RPCRemotingClient extends BaseRemoting {

    private ConfigurableInstance configurableInstance = new ClientConfigurableInstance();
    private ConnectionFactory connectionFactory = new RPCClientConnectionFactory(
            new ARPCCodec(),
            new HeartbeatHandler(),
            new BoltHandler(),
            configurableInstance);
    private ConnectionEventHandler connectionEventHandler = new ConnectionEventHandler(configurableInstance.switches());
    private ReconnectManager reconnectManager;
    private ConnectionEventListener connectionEventListener = new ConnectionEventListener();
    private RemotingAddressParser addressParser = ARPCAddressParser.PARSER;
    private ConnectionSelectStrategy connectionSelectStrategy = new RandomSelectStrategy(configurableInstance.switches());
    private DefaultConnectionManager connectionManager = new DefaultConnectionManager(
            connectionSelectStrategy,
            connectionFactory,
            connectionEventHandler,
            connectionEventListener,
            configurableInstance.switches());
    private DefaultConnectionMonitor connectionMonitor;
    private ConnectionMonitorStrategy monitorStrategy = new ScheduledDisconnectStrategy();

    /**
     * IO密集型处理器线程池
     */
    private final static ThreadPoolExecutor IMAGE_PROCESSOR_EXECUTOR = new ThreadPoolExecutor(
            500, 500, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(),
            r -> {
                Thread thread = new Thread(r);
                thread.setName("image-processor-thread");
                return thread;
            });

    /**
     * default constructor
     */
    protected RPCRemotingClient(CommandFactory commandFactory) {
        super(commandFactory);
        init();
    }

    private void init() {
        configurableInstance.switches().turnOn(GlobalSwitch.CONN_MONITOR_SWITCH);
        configurableInstance.switches().turnOn(GlobalSwitch.CONN_RECONNECT_SWITCH);
        configurableInstance.switches().turnOn(GlobalSwitch.SERVER_MANAGE_CONNECTION_SWITCH);
        configurableInstance.switches().turnOn(GlobalSwitch.SERVER_SYNC_STOP);
        this.connectionManager.setAddressParser(this.addressParser);
        this.connectionManager.init();
        if (configurableInstance.switches().isOn(GlobalSwitch.CONN_MONITOR_SWITCH)) {
            if (monitorStrategy == null) {
                ScheduledDisconnectStrategy strategy = new ScheduledDisconnectStrategy();
                connectionMonitor = new DefaultConnectionMonitor(strategy, this.connectionManager);
            } else {
                connectionMonitor = new DefaultConnectionMonitor(monitorStrategy, this.connectionManager);
            }
            connectionMonitor.start();
            log.warn("Switch on connection monitor");
        }
        if (configurableInstance.switches().isOn(GlobalSwitch.CONN_RECONNECT_SWITCH)) {
            reconnectManager = new ReconnectManager(connectionManager);
            connectionEventHandler.setReconnectManager(reconnectManager);
            log.warn("Switch on reconnect manager");
        }
        try {
            ProtocolManager.registerProtocol(ARPCProtocolV1.VIDEO_PROTOCOL, ARPCProtocolV1.PROTOCOL_CODE);
            ProtocolManager.getProtocol(ProtocolCode.fromBytes(ARPCProtocolV1.PROTOCOL_CODE)).getCommandHandler().registerDefaultExecutor(IMAGE_PROCESSOR_EXECUTOR);
            ProtocolManager.getProtocol(ProtocolCode.fromBytes(ARPCProtocolV1.PROTOCOL_CODE)).getCommandHandler().registerProcessor(RPCPackageCode.HEART_SYN_COMMAND, new ARPCHeardProcessor());
            ProtocolManager.getProtocol(ProtocolCode.fromBytes(ARPCProtocolV1.PROTOCOL_CODE)).getCommandHandler().registerProcessor(RPCPackageCode.HEART_ACK_COMMAND, new ARPCHeardProcessor());
        } catch (Exception e) {
            log.error("client注册协议失败", e);
        }
    }

    public Connection getAndCreateIfAbsent(String url) {
        try {
            Connection connection = connectionManager.getAndCreateIfAbsent(addressParser.parse(url));
            if (connection == null) {
                return null;
            }
            if (connection.getChannel().attr(Connection.CONNECTION).get() == null) {
                connection.getChannel().attr(Connection.CONNECTION).set(connection);
            }
            return connection;
        } catch (Exception e) {
            log.error("connect server error", e);
            return null;
        }
    }

    public void shutdown() {
        this.connectionManager.removeAll();
        log.warn("rpc client shutdown!");
        if (reconnectManager != null) {
            reconnectManager.stop();
        }
        if (connectionMonitor != null) {
            connectionMonitor.destroy();
        }
    }

    public RPCPackage invokeSync(final String url, final RemotingCommand request,
                                 final int timeoutMillis) throws RemotingException,
            InterruptedException {
        RPCPackage responseMessage = (RPCPackage) super.invokeSync(getAndCreateIfAbsent(url), request, timeoutMillis);
        return responseMessage;
    }

    public void invokeWithCallback(final String url, final RemotingCommand request,
                                   final InvokeCallback invokeCallback, final int timeoutMillis) {
        super.invokeWithCallback(getAndCreateIfAbsent(url), request, invokeCallback, timeoutMillis);
    }

    public InvokeFuture invokeWithFuture(final String url, final RemotingCommand request, final int timeoutMillis) {
        return super.invokeWithFuture(getAndCreateIfAbsent(url), request, timeoutMillis);
    }

    public void oneway(final String url, final RemotingCommand request) {
        super.oneway(getAndCreateIfAbsent(url), request);
    }


    @Override
    protected InvokeFuture createInvokeFuture(RemotingCommand request, InvokeContext invokeContext) {
        return new DefaultInvokeFuture(request.getId(), null, null, request.getProtocolCode().getFirstByte(), this.getCommandFactory(), invokeContext);

    }

    @Override
    protected InvokeFuture createInvokeFuture(Connection conn, RemotingCommand request, InvokeContext invokeContext, InvokeCallback invokeCallback) {
        return new DefaultInvokeFuture(request.getId(), new RpcInvokeCallbackListener(RemotingUtil.parseRemoteAddress(conn.getChannel())),
                invokeCallback, request.getProtocolCode().getFirstByte(), this.getCommandFactory(), invokeContext);
    }

}
