package com.nix.jingxun.addp.rpc.common.util;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * @author keray
 * @date 2018/08/28 下午2:18
 * qps 检查分钟内的qps
 */
@Slf4j
public final class QpsLimit {
    private static final ConcurrentMap<String,SlidingWindow> WINDOWS = new ConcurrentHashMap<>(16);
    private static final int DEFAULT_WAIT_COUNT = 10 * 1000;
    private static final int CLEAR_OVER_TIME = 10 * 60;
    static {
        new GuardianThread().start();
    }
    /**
     * 打到qps限制后的执行策略
     * */
    public enum Strategy{
        //拒绝抛出异常
        throw_exception,
        //等待qps降低到限制一下执行
        wait,
        //只是记录下日志
        noting_log,
        //不做任何操作
        noting,
    }

    /**
     * 切面限制qps
     * @param sign qps限制签名
     * @param qps qps限制数
     * @param strategy 超出限制后执行策略
     * @param wait 等待策略等待时间  null时默认 {@value DEFAULT_WAIT_COUNT} 毫秒
     * */
    public static void acceptLimit(String sign,long qps,Strategy strategy,Integer wait) throws TimeoutException {
        wait = wait == null ? DEFAULT_WAIT_COUNT : wait;
        if (checkQps(sign,qps)) {
            switch (strategy) {
                case throw_exception:throw new RuntimeException("qps limit fail");
                case wait:wait(sign,wait);break;
                case noting:break;
                case noting_log:log.warn("{} :qps to achieve limit . limit {}",Thread.currentThread().getName(),qps);break;
                default:break;
            }
        }
    }

    /**
     * 方法执行完成调用 将并发qps减一
     * @param sign 签名
     * */
    public static void acceptCancel(String sign) {
        WINDOWS.get(sign).cancel();
    }

    public static void cancel(String sign) {
        WINDOWS.remove(sign);
    }

    /**
     * 切面qps限制
     * @param sign
     * @param qps
     * @param pass 限制通过（没达到qps限制）执行函数
     * @param refused 限制未通过 执行函数
     * @return
     * */
    public static <R> R acceptLimit(String sign, long qps, Supplier<R> pass,Supplier<R> refused ) {
        if (checkQps(sign,qps)) {
            return pass.get();
        }
        return refused.get();
    }

    /**
     * 切面qps限制
     * @param sign
     * @param qps
     * @param refused
     * @return
     * */
    public static <R> R acceptLimit(String sign, long qps,Supplier<R> refused ) {
        if (checkQps(sign, qps)) {
            return null;
        }
        return refused.get();
    }

    private static void wait(String sign,int time) throws TimeoutException {
        final Object notify = WINDOWS.get(sign).notify;
        synchronized (notify) {
            try {
                WINDOWS.get(sign).startWait();
                long start = System.currentTimeMillis();
                notify.wait(time);
                long end = System.currentTimeMillis();
                WINDOWS.get(sign).endWait();
                if (end - start >= time) {
                    throw new TimeoutException("wait notify timeout");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getCause());
            }
        }
    }

    /**
     * 检查调用qps是否超过限制
     * */
    private static boolean checkQps(String sign,long qps) {
        if (!WINDOWS.containsKey(sign)) {
            synchronized (QpsLimit.class) {
                if (!WINDOWS.containsKey(sign)) {
                    WINDOWS.put(sign,new SlidingWindow(sign,qps));
                    return false;
                }
            }
        }
        return WINDOWS.get(sign).clickQps();
    }
    @ToString
    private static class SlidingWindow {
        private final String sign;
        private final long limitQps;
        //分段 一段15秒
        private static final int PIECEWISE = 4;
        //qps取样4段 15*4 60秒
        private static final int ALL_PIECEWISE = 15;
        private volatile long start = 0;
        private volatile long end = 0;
        //一分钟分四段 每15秒设置一段
        private volatile int[] slidingRecord = new int[ALL_PIECEWISE];
        private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        private final Object lastWindowCLock = new Object();
        // 唤醒
        private final Object notify = new Object();
        // 等待计数
        private final AtomicInteger waitCount = new AtomicInteger();
        private SlidingWindow(String sign, long limitQps) {
            this.sign = sign;
            this.limitQps = limitQps;
            init();
        }
        private void init() {
            for (int i = 0;i < ALL_PIECEWISE - 1;i ++) {
                slidingRecord[i] = 0;
            }
            slidingRecord[ALL_PIECEWISE - 1] = 1;
            start = nowSeconds() - (PIECEWISE - 1) * ALL_PIECEWISE;
            end = nowSeconds();
        }
        private boolean clickQps() {
            long now = nowSeconds();
            long nowQps;
            //保证滑动操作只执行一次 保证获取最新qps和窗口滑动原子性
            if (now >= end + PIECEWISE) {
                //乐观锁
                synchronized (sign) {
                    if (now >= end + PIECEWISE) {
                        sliding();
                    }
                }
            }
            nowQps = nowQps();
            return nowQps > limitQps;
        }

        /**
         * 窗口滑动
         * 保证只能有一个线程进行滑动操作
         * */
        private void sliding() {
            ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();
            try {
                //获取这次qps之前的起始窗口
                long agoStart = start;
                start = nowSeconds() - (PIECEWISE - 1) * ALL_PIECEWISE;
                end = nowSeconds();
                //获取窗口开始到现在的窗口跨度
                int span = (int) ((start - agoStart) /PIECEWISE);
                writeLock.lock();
                //如果跨度大于窗口总大小
                if (span >= PIECEWISE) {
                    for (int i = 0;i < ALL_PIECEWISE;i ++) {
                        slidingRecord[i] = 0;
                    }
                } else {
                    //跨度小于窗口数量 按跨度空隙进行滑动窗口
                    System.arraycopy(slidingRecord, span, slidingRecord, 0, ALL_PIECEWISE - span);
                    for (int i = ALL_PIECEWISE - span;i < ALL_PIECEWISE;i ++) {
                        slidingRecord[i] = 0;
                    }
                }
            }finally {
                writeLock.unlock();
            }
        }
        /**
         * 获取当前的qps
         * */
        private long nowQps() {
            ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
            try {
                readLock.lock();
                long qps = 0;
                for (int i = 0;i < ALL_PIECEWISE - 1;i ++) {
                    qps += slidingRecord[i];
                }
                synchronized (lastWindowCLock) {
                    qps += ++ slidingRecord[ALL_PIECEWISE - 1];
                }
                return qps;
            }finally {
                readLock.unlock();
            }
        }

        /**
         * 添加一个等待的请求
         * */
        public void startWait() {
            waitCount.getAndIncrement();
        }

        /**
         * 释放一个等待请求
         * */
        public void endWait() {
            waitCount.getAndAdd(-1);
        }

        private long nowSeconds() {
            return System.currentTimeMillis() / 1_000;
        }

        /**
         * 执行完成 降低qps
         * */
        public void cancel() {
            synchronized (lastWindowCLock) {
                slidingRecord[ALL_PIECEWISE - 1] -= 1L;
                if (waitCount.get() > 0) {
                    synchronized (notify) {
                        notify.notify();
                    }
                }
            }
        }
    }

    private static class GuardianThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    WINDOWS.entrySet().removeIf(entry -> entry.getValue().end - CLEAR_OVER_TIME > System.currentTimeMillis() / 1_000);
                }catch (Exception e) {
                    log.warn("guardian thread clear windows exception ",e);
                }
                try {
                    sleep(1000 * 60 * 10);
                } catch (InterruptedException e) {
                    log.error("guardian thread clear windows interrupted . stop!!!");
                    return;
                }
            }
        }
    }
}

