package com.nix.jingxun.addp.web.common.supper;

import com.alibaba.fastjson.JSON;
import com.nix.jingxun.addp.web.base.SpringContextHolder;
import com.nix.jingxun.addp.web.common.DistributedLock;
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * @author keray
 * @date 2019/06/04 9:55
 */
@Component
public class RedisLock implements DistributedLock {


    @Resource
    private RedisTemplate<String, String> template;


    @Override
    public void tryLock(String key, Consumer<String> callback) throws InterruptedException {
        try {
            tryLock(key, callback,0);
        } catch (TimeoutException ignore) {
        }
    }
    @Override
    public void tryLock(String key, Consumer<String> callback, long timeout) throws InterruptedException, TimeoutException {
        long now = System.currentTimeMillis();
        Thread currentThread = Thread.currentThread();
        int type = 0;
        while (true) {
            if (!(System.currentTimeMillis() - now < timeout || timeout == 0)) {
                type = 1;
                break;
            }
            if (currentThread.isInterrupted()) {
                type = 2;
                break;
            }
            String json = template.opsForValue().get(key);
            if (json == null) {
                OLock lock = new OLock();
                lock.setHash(currentThread.hashCode());
                lock.setCount(1);
                Boolean result = template.opsForValue().setIfAbsent(key,lock.toJson());
                if (result != null && result) {
                    // 加锁成功
                    execCallback(key,callback);
                    break;
                }
            } else {
                OLock lock = JSON.parseObject(json,OLock.class);
                if (lock.getHash() == currentThread.hashCode()) {
                    lock.setCount(lock.getCount() + 1);
                    template.opsForValue().set(key,lock.toJson());
                    // 获取锁成功
                    execCallback(key,callback);
                    break;
                }
            }
        }
        if (type == 2) {
            throw new InterruptedException("thread interrupted");
        }
        if (type == 1) {
            throw new TimeoutException("get lock " + key + "timeout : " + timeout);
        }
    }

    @Override
    public void tryLock(String key, long timeout) throws TimeoutException, InterruptedException {
        tryLock(key,null,timeout);
    }

    @Override
    public void tryLock(String key) throws InterruptedException {
        try {
            tryLock(key,0);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unLock(String key) {
        OLock lock = JSON.parseObject(template.opsForValue().get(key),OLock.class);
        if (lock == null) {
            return;
        }
        lock.setCount(lock.getCount() - 1);
        if (lock.getCount() == 0) {
            template.delete(key);
        } else {
            template.opsForValue().set(key,lock.toJson());
        }
    }

    private void execCallback(String key, Consumer<String> callback) {
        try {
           if (callback != null) {
               callback.accept(key);
           }
        }catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            unLock(key);
        }
    }



    private static DistributedLock distributedLock;

    public static void lock(String key,Consumer<String> callback) throws TimeoutException, InterruptedException {
        if (distributedLock == null) {
            synchronized (RedisLock.class) {
                if (distributedLock == null) {
                    distributedLock = SpringContextHolder.getBean(DistributedLock.class);
                }
            }
        }
        distributedLock.tryLock(key,callback,10 * 60 * 1000);
    }
    public static void lock(String key) throws TimeoutException, InterruptedException {
        if (distributedLock == null) {
            synchronized (RedisLock.class) {
                if (distributedLock == null) {
                    distributedLock = SpringContextHolder.getBean(DistributedLock.class);
                }
            }
        }
        distributedLock.tryLock(key,10 * 60 * 1000);
    }
    public static void unlock(String key) {
        distributedLock.unLock(key);
    }
}

@Data
class OLock implements Serializable {
    int hash;
    int count;
    String toJson() {
        return JSON.toJSONString(this);
    }
}
