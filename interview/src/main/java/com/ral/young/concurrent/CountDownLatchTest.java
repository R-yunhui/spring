package com.ral.young.concurrent;

import jodd.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@link java.util.concurrent.CountDownLatch} 门栓
 *
 * @author renyunhui
 * @date 2023-02-08 16:46
 * @since 1.0.0
 */
@Slf4j
public class CountDownLatchTest {

    static CountDownLatch countDownLatch = new CountDownLatch(3);
    static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 8, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(), ThreadFactoryBuilder.create().setNameFormat("work-thread-%d").get());

    public static void main(String[] args) throws InterruptedException {
        log.info("等待所有骑手准备完毕");
        threadPoolExecutor.execute(() -> {
            // 等到不存在任何阻碍的门栓之后开始执行后续的逻辑
            try {
                countDownLatch.await();
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("所有骑手已经准备完毕");
            threadPoolExecutor.shutdown();
        });

        for (int i = 0, n = 3; i < n; i++) {
            threadPoolExecutor.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(5 + ThreadLocalRandom.current().nextInt(5));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // 用来关闭阻碍的门栓
                countDownLatch.countDown();
                log.info("第{}号骑手准备就绪", Thread.currentThread().getId());
            });
        }


    }
}
