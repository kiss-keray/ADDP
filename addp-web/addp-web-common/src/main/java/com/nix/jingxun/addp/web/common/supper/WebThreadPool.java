package com.nix.jingxun.addp.web.common.supper;

import com.nix.jingxun.addp.web.common.config.WebConfig;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author keray
 * @date 2019/05/21 16:03
 */
public final class WebThreadPool {
    public static final ThreadPoolExecutor IO_THREAD = new ThreadPoolExecutor(
            WebConfig.ioThreadPoolMin, WebConfig.ioThreadPoolMax, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>(WebConfig.ioFutureMax),
            r -> {
                Thread t = new Thread(r);
                t.setName("addp-io");
                return t;
            });
}
