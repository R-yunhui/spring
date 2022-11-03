package com.ral.young.concurrent.threadpool;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@link java.util.concurrent.ScheduledThreadPoolExecutor}
 *
 * @author renyunhui
 * @date 2022-10-17 13:54
 * @since 1.0.0
 */
@Slf4j
public class ScheduledThreadPoolExecutorDemo {

    public static void main(String[] args) {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutorOne = new ScheduledThreadPoolExecutor(5, ThreadFactoryBuilder.create().setNamePrefix("scheduledOne-").build());

        // 按照固定的速率执行任务，如果存在任务执行时间超过了这个固定的速率，则在当前任务完成之后会立刻执行下一个任务
        scheduledThreadPoolExecutorOne.scheduleAtFixedRate(new TaskDemoOne(10L), 0, 5, TimeUnit.SECONDS);

        // 按照固定的延迟执行任务，如果存在任务执行时间超过了这个固定的延迟，则在当前任务完成之后再次等待这个固定的延迟时间之后再执行
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutorTwo = new ScheduledThreadPoolExecutor(5, ThreadFactoryBuilder.create().setNamePrefix("scheduledTwo-").build());
        scheduledThreadPoolExecutorTwo.scheduleWithFixedDelay(new TaskDemoOne(8L), 5, 5, TimeUnit.SECONDS);
    }

    public static class TaskDemoOne implements Runnable {

        private final Long sleepTime;

        public TaskDemoOne(Long sleepTime) {
            this.sleepTime = sleepTime;
        }

        @Override
        public void run() {
            log.info("TaskDemo run,start time:{}", DateUtil.now());
            try {
                TimeUnit.SECONDS.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.info("TaskDemo run,end time:{}", DateUtil.now());
        }
    }
}
