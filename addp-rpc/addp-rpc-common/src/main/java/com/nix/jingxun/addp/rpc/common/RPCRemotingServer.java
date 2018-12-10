package com.nix.jingxun.addp.rpc.common;

import com.alipay.remoting.*;
import com.alipay.remoting.codec.Codec;
import com.alipay.remoting.config.ConfigManager;
import com.alipay.remoting.config.switches.GlobalSwitch;
import com.alipay.remoting.rpc.protocol.RpcHeartBeatProcessor;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import com.alipay.remoting.util.NettyEventLoopUtil;
import com.alipay.remoting.util.RemotingUtil;
import com.nix.jingxun.addp.rpc.common.protocol.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author keray
 * @date 2018/10/19 2:27 PM
 */
@Slf4j
public class RPCRemotingServer extends AbstractRemotingServer {
    /**
     * IO密集型处理器线程池
     */
    protected final static ThreadPoolExecutor IMAGE_PROCESSOR_EXECUTOR = new ThreadPoolExecutor(
            500, 500, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(),
            r -> {
                Thread thread = new Thread(r);
                thread.setName("image-processor-thread");
                return thread;
            });
    protected ChannelFuture channelFuture;
    protected ServerBootstrap bootstrap;
    protected ConnectionEventHandler connectionEventHandler;
    protected DefaultConnectionManager connectionManager;
    protected RemotingAddressParser addressParser;
    protected ConnectionEventListener connectionEventListener = new ConnectionEventListener();
    protected static final EventLoopGroup BOSS_GROUP = NettyEventLoopUtil.newEventLoopGroup(1,
            new NamedThreadFactory(
                    "Rpc-netty-server-boss",
                    false));
    protected static final EventLoopGroup WORKER_GROUP = NettyEventLoopUtil.newEventLoopGroup(
            Runtime.getRuntime().availableProcessors() * 2,
            new NamedThreadFactory(
                    "Rpc-netty-server-worker",
                    true));
    protected Codec codec = new ARPCCodec();
    protected final ChannelHandler serverIdleHandler;

    protected RPCRemotingServer(int port, ChannelHandler serverIdleHandler) {
        super(port);
        this.serverIdleHandler = serverIdleHandler;
    }

    @Override
    public void registerProcessor(byte protocolCode, CommandCode commandCode, RemotingProcessor<?> processor) {
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(protocolCode)).getCommandHandler().registerProcessor(commandCode, processor);
    }

    @Override
    public void registerDefaultExecutor(byte protocolCode, ExecutorService executor) {
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(protocolCode)).getCommandHandler().registerDefaultExecutor(executor);
    }

    @Override
    public void registerUserProcessor(UserProcessor<?> processor) {

    }

    @Override
    protected void doInit() {
        //启动配置开关
        switches().turnOn(GlobalSwitch.SERVER_SYNC_STOP);
        switches().turnOn(GlobalSwitch.CONN_MONITOR_SWITCH);
        switches().turnOn(GlobalSwitch.SERVER_MANAGE_CONNECTION_SWITCH);

        if (this.addressParser == null) {
            this.addressParser = ARPCAddressParser.PARSER;
        }
        this.connectionManager = new DefaultConnectionManager(new RandomSelectStrategy());
        this.connectionEventHandler = new ConnectionEventHandler(switches());
        this.connectionEventHandler.setConnectionManager(this.connectionManager);
        this.connectionEventHandler.setConnectionEventListener(this.connectionEventListener);
        this.connectionEventHandler.setReconnectManager(new ReconnectManager(this.connectionManager));
        // 设置netty server
        this.bootstrap = new ServerBootstrap();
        this.bootstrap.group(BOSS_GROUP, WORKER_GROUP)
                .channel(NettyEventLoopUtil.getServerSocketChannelClass())
                .option(ChannelOption.SO_BACKLOG, ConfigManager.tcp_so_backlog())
                .option(ChannelOption.SO_REUSEADDR, ConfigManager.tcp_so_reuseaddr())
                .childOption(ChannelOption.TCP_NODELAY, ConfigManager.tcp_nodelay())
                .childOption(ChannelOption.SO_KEEPALIVE, ConfigManager.tcp_so_keepalive());
        initWriteBufferWaterMark();
        this.bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        NettyEventLoopUtil.enableTriggeredMode(bootstrap);
        final boolean idleSwitch = ConfigManager.tcp_idle_switch();
        final int idleTime = ConfigManager.tcp_server_idle();
        final ChannelHandler rootHandler = new BoltHandler();
        this.bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast("decoder", codec.newDecoder());
                pipeline.addLast("encoder", codec.newEncoder());
                if (idleSwitch) {
                    pipeline.addLast("idleStateHandler", new IdleStateHandler(10, 10, idleTime, TimeUnit.MINUTES));
                    pipeline.addLast("serverIdleHandler", serverIdleHandler);
                }
                pipeline.addLast("connectionEventHandler", connectionEventHandler);
                pipeline.addLast("handler", rootHandler);
                createConnection(channel);
            }

            /**
             * 管理链接
             */
            private void createConnection(SocketChannel channel) {
                Url url = addressParser.parse(RemotingUtil.parseRemoteAddress(channel));
                if (switches().isOn(GlobalSwitch.SERVER_MANAGE_CONNECTION_SWITCH)) {
                    connectionManager.add(new Connection(channel, url), url.getUniqueKey());
                } else {
                    new Connection(channel, url);
                }
            }
        });
        // 注册协议处理器
        ProtocolManager.registerProtocol(ARPCProtocolV1.VIDEO_PROTOCOL, ARPCProtocolV1.PROTOCOL_CODE);
        registerDefaultExecutor(ARPCProtocolV1.PROTOCOL_CODE, IMAGE_PROCESSOR_EXECUTOR);
        registerProcessor(ARPCProtocolV1.PROTOCOL_CODE, RPCPackageCode.HEART_SYN_COMMAND, new ARPCHeardProcessor());
        registerProcessor(ARPCProtocolV1.PROTOCOL_CODE, RPCPackageCode.HEART_ACK_COMMAND, new ARPCHeardProcessor());
    }

    @Override
    protected boolean doStart() throws InterruptedException {
        this.channelFuture = this.bootstrap.bind(new InetSocketAddress(ip(), port())).sync();
        return this.channelFuture.isSuccess();
    }

    @Override
    protected boolean doStop() {
        if (null != this.channelFuture) {
            this.channelFuture.channel().close();
        }
        if (this.switches().isOn(GlobalSwitch.SERVER_SYNC_STOP)) {
            BOSS_GROUP.shutdownGracefully().awaitUninterruptibly();
        } else {
            BOSS_GROUP.shutdownGracefully();
        }
        if (this.switches().isOn(GlobalSwitch.SERVER_MANAGE_CONNECTION_SWITCH)
                && null != this.connectionManager) {
            this.connectionManager.removeAll();
            log.warn("Close all connections from server side!");
        }
        IMAGE_PROCESSOR_EXECUTOR.shutdown();
        log.warn("video Server stopped!");
        return true;
    }

    private void initWriteBufferWaterMark() {
        int lowWaterMark = this.netty_buffer_low_watermark();
        int highWaterMark = this.netty_buffer_high_watermark();
        if (lowWaterMark > highWaterMark) {
            throw new IllegalArgumentException(
                    String.format("[server side] bolt netty high water mark {%s} should not be smaller than low water mark {%s} bytes)", highWaterMark, lowWaterMark));
        } else {
            log.warn("[server side] bolt netty low water mark is {} bytes, high water mark is {} bytes", lowWaterMark, highWaterMark);
        }
        this.bootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(lowWaterMark, highWaterMark));
    }

    public DefaultConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
