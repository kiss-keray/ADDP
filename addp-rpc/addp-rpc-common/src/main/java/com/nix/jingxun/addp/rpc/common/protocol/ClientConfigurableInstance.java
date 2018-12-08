package com.nix.jingxun.addp.rpc.common.protocol;

import com.alipay.remoting.config.ConfigurableInstance;
import com.alipay.remoting.config.configs.ConfigContainer;
import com.alipay.remoting.config.switches.GlobalSwitch;

/**
 * @author keray
 * @date 2018/10/31 9:56 PM
 */
public class ClientConfigurableInstance implements ConfigurableInstance {
    /**
     * get the config container for current instance
     *
     * @return the config container
     */
    @Override
    public ConfigContainer conf() {
        return null;
    }

    /**
     * get the global switch for current instance
     *
     * @return the global switch
     */
    @Override
    public GlobalSwitch switches() {
        return new GlobalSwitch();
    }

    /**
     * Initialize netty write buffer water mark for remoting instance.
     * <p>
     * Notice: This api should be called before init remoting instance.
     *
     * @param low  [0, high]
     * @param high [high, Integer.MAX_VALUE)
     */
    @Override
    public void initWriteBufferWaterMark(int low, int high) {

    }

    /**
     * get the low water mark for netty write buffer
     *
     * @return low watermark
     */
    @Override
    public int netty_buffer_low_watermark() {
        return 0;
    }

    /**
     * get the high water mark for netty write buffer
     *
     * @return high watermark
     */
    @Override
    public int netty_buffer_high_watermark() {
        return 0;
    }
}
