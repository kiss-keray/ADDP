package com.nix.jingxun.addp.rpc.common.protocol;

import com.alipay.remoting.*;

/**
 * @author keray
 * @date 2018/10/19 3:56 PM
 */
public class ARPCProtocolV1 implements Protocol {
    public static final byte PROTOCOL_CODE = 0x01;
    /**
     * PROTOCOL_CODE
     */
    public static final int HEADER_LEN = 1;

    public static final int HEADER_DATA_LEN = 12;
    public static final byte VERSION = 1;
    private final static CommandEncoder ENCODER = new ARPCEncoder();
    private final static CommandDecoder DECODER = new ARPCDecoder();
    private final static HeartbeatTrigger HEARTBEAT_TRIGGER = new ARPCHeartbeatTrigger();
    private static final CommandHandler COMMAND_HANDLER = new ARPCCommandHandler();
    private static final CommandFactory COMMAND_FACTORY = new ARPCCommandFactory();
    public static final ARPCProtocolV1 VIDEO_PROTOCOL = new ARPCProtocolV1();

    private ARPCProtocolV1() {
    }

    /**
     * Get the newEncoder for the protocol.
     *
     * @return
     */
    @Override
    public CommandEncoder getEncoder() {
        return ENCODER;
    }

    /**
     * Get the decoder for the protocol.
     *
     * @return
     */
    @Override
    public CommandDecoder getDecoder() {
        return DECODER;
    }

    /**
     * Get the heartbeat trigger for the protocol.
     *
     * @return
     */
    @Override
    public HeartbeatTrigger getHeartbeatTrigger() {
        return HEARTBEAT_TRIGGER;
    }

    /**
     * Get the command handler for the protocol.
     *
     * @return
     */
    @Override
    public CommandHandler getCommandHandler() {
        return COMMAND_HANDLER;
    }

    /**
     * Get the command factory for the protocol.
     *
     * @return
     */
    @Override
    public CommandFactory getCommandFactory() {
        return COMMAND_FACTORY;
    }


}
