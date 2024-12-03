package com.ral.young.metrics.monitor;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.RandomUtil;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author renyunhui
 * @description 这是一个ThreadPoolMonitorService类
 * @date 2024-12-02 15-54-17
 * @since 1.0.0
 */
@Component
@Slf4j
public class ThreadPoolMonitorService implements InitializingBean, DisposableBean {

    private static final ConcurrentMap<String, ThreadPoolExecutor> threadPoolMap = new ConcurrentHashMap<>();

    static {
        ThreadPoolExecutor poolOne = new ThreadPoolExecutor(4, 8, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(15)
                , new ThreadFactoryBuilder().setNamePrefix("pool-one").build(), new ThreadPoolExecutor.CallerRunsPolicy());

        ThreadPoolExecutor poolTwo = new ThreadPoolExecutor(4, 8, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(20)
                , new ThreadFactoryBuilder().setNamePrefix("pool-two").build(), new ThreadPoolExecutor.CallerRunsPolicy());

        ThreadPoolExecutor poolThree = new ThreadPoolExecutor(4, 8, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(25)
                , new ThreadFactoryBuilder().setNamePrefix("pool-three").build(), new ThreadPoolExecutor.CallerRunsPolicy());

        ThreadPoolExecutor poolFour = new ThreadPoolExecutor(4, 8, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(30)
                , new ThreadFactoryBuilder().setNamePrefix("pool-four").build(), new ThreadPoolExecutor.CallerRunsPolicy());

        threadPoolMap.put("pool-one", poolOne);
        threadPoolMap.put("pool-two", poolTwo);
        threadPoolMap.put("pool-three", poolThree);
        threadPoolMap.put("pool-four", poolFour);
    }

    @Resource
    private MeterRegistry meterRegistry;

    public void registerMetrics() {
        threadPoolMap.forEach((threadPoolName, threadPoolExecutor) -> {
            // 目前正在执行任务的线程数
            meterRegistry.gauge("thread.pool.active.count", Tags.of("poolName", threadPoolName), threadPoolExecutor, ThreadPoolExecutor::getActiveCount);
            // 线程池的核心线程数
            meterRegistry.gauge("thread.pool.core.count", Tags.of("poolName", threadPoolName), threadPoolExecutor, ThreadPoolExecutor::getCorePoolSize);
            // 当前任务队列大小
            meterRegistry.gauge("thread.pool.queue.size", Tags.of("poolName", threadPoolName), threadPoolExecutor, e -> e.getQueue().size());
            // 完成的任务数
            meterRegistry.gauge("thread.pool.completed.task.count", Tags.of("poolName", threadPoolName), threadPoolExecutor, ThreadPoolExecutor::getCompletedTaskCount);
            // 任务总数
            meterRegistry.gauge("thread.pool.task.count", Tags.of("poolName", threadPoolName), threadPoolExecutor, ThreadPoolExecutor::getTaskCount);

            log.info("{} 线程池埋点成功", threadPoolName);
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        registerMetrics();
    }

    @Scheduled(fixedRate = 21000)
    public void testOne() {
        IntStream.range(0, 10).forEach(o -> {
            threadPoolMap.get("pool-one").execute(() -> {
                try {
                    log.info("{} 开始执行任务", Thread.currentThread().getName());
                    int random = RandomUtil.randomInt(5, 20);
                    Thread.sleep(random * 1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    @Scheduled(fixedRate = 22000)
    public void testTwo() {
        IntStream.range(0, 20).forEach(o -> {
            threadPoolMap.get("pool-two").execute(() -> {
                try {
                    log.info("{} 第二个开始执行任务", Thread.currentThread().getName());
                    int random = RandomUtil.randomInt(5, 20);
                    Thread.sleep(random * 1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    @Scheduled(fixedRate = 23000)
    public void testThree() {
        IntStream.range(0, 30).forEach(o -> {
            threadPoolMap.get("pool-three").execute(() -> {
                try {
                    log.info("{} 第三个开始执行任务", Thread.currentThread().getName());
                    int random = RandomUtil.randomInt(5, 20);
                    Thread.sleep(random * 1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    @Scheduled(fixedRate = 24000)
    public void testFour() {
        IntStream.range(0, 40).forEach(o -> {
            threadPoolMap.get("pool-four").execute(() -> {
                try {
                    log.info("{} 第四个开始执行任务", Thread.currentThread().getName());
                    int random = RandomUtil.randomInt(5, 20);
                    Thread.sleep(random * 1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    @Override
    public void destroy() throws Exception {
        threadPoolMap.values().forEach(pool -> {
            pool.shutdown();
            try {
                if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.info("Executor did not terminate in the specified time.");
                    List<Runnable> droppedTasks = pool.shutdownNow();
                    log.info("Executor was abruptly shut down. {} tasks will not be executed.", droppedTasks.size());
                }
            } catch (InterruptedException e) {
                log.error("线程中断异常 : ", e);
            }
        });
    }
}
