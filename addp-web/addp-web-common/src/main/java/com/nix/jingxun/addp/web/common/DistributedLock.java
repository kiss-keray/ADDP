package com.nix.jingxun.addp.web.common;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * @author keray
 * @date 2019/06/04 9:44
 * 分布式锁
 */
public interface DistributedLock extends Serializable {
    void tryLock(String key,Consumer<String> callback) throws InterruptedException;
    void tryLock(String key,Consumer<String> callback,long timeout) throws InterruptedException, TimeoutException;

    void tryLock(String key,long timeout) throws TimeoutException, InterruptedException;
    void tryLock(String key) throws InterruptedException;

    void unLock(String key);
}
