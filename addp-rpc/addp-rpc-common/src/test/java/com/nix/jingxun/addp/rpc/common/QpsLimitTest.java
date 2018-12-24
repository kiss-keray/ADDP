package com.nix.jingxun.addp.rpc.common;

import com.nix.jingxun.addp.rpc.common.util.QpsLimit;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author keray
 * @date 2018/08/29 上午10:24
 */
public class QpsLimitTest {
    private final static ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(1000, 1000, 0, TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<>(), new ThreadFactory() {
        private final AtomicLong ATOMIC_LONG = new AtomicLong(0);
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("concurrent_" + ATOMIC_LONG.getAndIncrement());
            return thread;
        }
    });
    private final static CountDownLatch DOWN_LATCH = new CountDownLatch(9000);
    private final AtomicLong OPS_COUNT = new AtomicLong(0);

    public void testNothingApi() throws TimeoutException {
        QpsLimit.acceptLimit("testApi",2900, QpsLimit.Strategy.noting_log,null);
        DOWN_LATCH.countDown();
    }

    public void testThrowApi() {
        try {
            QpsLimit.acceptLimit("testApi",2900, QpsLimit.Strategy.throw_exception,null);
        }catch (Exception e) {
            synchronized (OPS_COUNT) {
                OPS_COUNT.getAndIncrement();
                System.out.println(OPS_COUNT);
            }
        }
        DOWN_LATCH.countDown();
    }

    @Test
    public void qpsTest() throws TimeoutException {
        for (int i = 0;i < 10;i ++) {
            testNothingApi();
        }
    }

    @Test
    public void concurrentTest() throws InterruptedException {
        long now = System.currentTimeMillis();
        for (int i = 0;i < 30;i ++) {
            for (int j = 0; j < 300; j++) {
                THREAD_POOL_EXECUTOR.execute(() -> testThrowApi());
            }
            if (i == 19) {
                try {
                    Thread.sleep(21000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        DOWN_LATCH.await();
        System.out.println((System.currentTimeMillis() - now));
    }

    private final static int count = 1_0_000;
    final AtomicLong performance = new AtomicLong(0);
    private volatile CountDownLatch DOWN_LATCH_1 = new CountDownLatch(count);

    //进行QPS验证执行的方法
    private void performance(){
        try {
            QpsLimit.acceptLimit("performance",count - 882, QpsLimit.Strategy.throw_exception,null);
        }catch (Exception e) {
            if ("qps limit fail".equalsIgnoreCase(e.getMessage())) {
                OPS_COUNT.getAndIncrement();
            } else {
                e.printStackTrace();
            }
        }
        performance.getAndIncrement();
        DOWN_LATCH_1.countDown();
    }


    final AtomicLong noPerformance = new AtomicLong(0);
    private volatile CountDownLatch DOWN_LATCH_2 = new CountDownLatch(count);
    //进行QPS验证执行的方法
    private void noPerformance(){
        noPerformance.getAndIncrement();
        DOWN_LATCH_2.countDown();
    }

    @Test
    public void concurrentTest_1() throws InterruptedException {
        for (int k = 0;k < 10;k ++ ) {
            DOWN_LATCH_1 = new CountDownLatch(count);
            performance.set(0);
            OPS_COUNT.set(0);
            long now = System.currentTimeMillis();
            for (int i = 0; i < count / 10000; i++) {
                for (int j = 0; j < 10000; j++) {
                    THREAD_POOL_EXECUTOR.execute(this::performance);
                }
            }
            DOWN_LATCH_1.await();
            System.out.println("-----");
            System.out.println("time : " + (System.currentTimeMillis() - now));
            System.out.println("over : " + OPS_COUNT);
            QpsLimit.cancel("performance");
        }
    }

    @Test
    public void testPerformance() throws InterruptedException {
        DOWN_LATCH_2 = new CountDownLatch(count);
        noPerformance.set(0);
        for (int i = 0; i < count / 10000; i++) {
            for (int j = 0; j < 10000; j++) {
                THREAD_POOL_EXECUTOR.execute(this::noPerformance);
            }
        }
        DOWN_LATCH_2.await();
        System.out.println("预热完成");

//////////////////


        DOWN_LATCH_2 = new CountDownLatch(count);
        noPerformance.set(0);
        long now1 = System.currentTimeMillis();
        for (int i = 0; i < count / 10000; i++) {
            for (int j = 0; j < 10000; j++) {
                THREAD_POOL_EXECUTOR.execute(this::noPerformance);
            }
        }
        DOWN_LATCH_2.await();
        System.out.println("time : " + (System.currentTimeMillis() - now1));
        System.out.println("result:" + noPerformance.get());

//////////////////

        DOWN_LATCH_1 = new CountDownLatch(count);
        performance.set(0);
        OPS_COUNT.set(0);
        long now = System.currentTimeMillis();
        for (int i = 0; i < count / 10000; i++) {
            for (int j = 0; j < 10000; j++) {
                THREAD_POOL_EXECUTOR.execute(this::performance);
            }
        }
        DOWN_LATCH_1.await();
        System.out.println("qps time : " + (System.currentTimeMillis() - now));
        System.out.println("qps over : " + OPS_COUNT);
        System.out.println("result:" + performance.get());
    }

    @Test
    public void testResult() throws InterruptedException {
        DOWN_LATCH_1 = new CountDownLatch(count);
        performance.set(0);
        OPS_COUNT.set(0);
        long now = System.currentTimeMillis();
        for (int i = 0; i < count / 10000; i++) {
            for (int j = 0; j < 10000; j++) {
                THREAD_POOL_EXECUTOR.execute(this::performance);
            }
        }
        DOWN_LATCH_1.await();
        System.out.println("qps time : " + (System.currentTimeMillis() - now));
        System.out.println("qps over : " + OPS_COUNT);
        System.out.println("result:" + performance.get());
    }

    @Test
    public void testResult_1() throws InterruptedException {
        DOWN_LATCH_2 = new CountDownLatch(count);
        noPerformance.set(0);
        long now1 = System.currentTimeMillis();
        for (int i = 0; i < count / 10000; i++) {
            for (int j = 0; j < 10000; j++) {
                THREAD_POOL_EXECUTOR.execute(this::noPerformance);
            }
        }
        DOWN_LATCH_2.await();
        System.out.println("time : " + (System.currentTimeMillis() - now1));
        System.out.println("result:" + noPerformance.get());
    }



    private final CountDownLatch latch = new CountDownLatch(100);
    public void testWaitApi() {
        try {
            QpsLimit.acceptLimit("testApi",100 - 1, QpsLimit.Strategy.wait,10);
            TimeUnit.MILLISECONDS.sleep(100);
            QpsLimit.acceptCancel("testApi");
        }catch (TimeoutException e) {
            OPS_COUNT.getAndIncrement();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            latch.countDown();
        }
    }

    @Test
    public void testWait() throws InterruptedException {
        long now1 = System.currentTimeMillis();
        for (int j = 0; j < 100; j++) {
            THREAD_POOL_EXECUTOR.execute(this::testWaitApi);
        }
        latch.await();
        System.out.println("over :" + OPS_COUNT.get());
        System.out.println("time : " + (System.currentTimeMillis() - now1));
    }
}
