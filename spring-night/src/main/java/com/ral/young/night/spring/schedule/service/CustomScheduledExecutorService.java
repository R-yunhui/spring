package com.ral.young.night.spring.schedule.service;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 需要自定义一个 CustomScheduledExecutorService 实现 ScheduledExecutorService，实现多线程异步执行 Spring 的定时任务
 * @author renyunhui
 * @date 2024-06-12 13:59
 * @since 1.0.0
 */
@Component
public class CustomScheduledExecutorService implements ScheduledExecutorService {

    private static final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(5, ThreadFactoryBuilder.create().setNamePrefix("custom-scheduled-task").build());

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return scheduledThreadPoolExecutor.schedule(command, delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return scheduledThreadPoolExecutor.schedule(callable, delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return scheduledThreadPoolExecutor.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return scheduledThreadPoolExecutor.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    @Override
    public void shutdown() {
        scheduledThreadPoolExecutor.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return Collections.emptyList();
    }

    @Override
    public boolean isShutdown() {
        return scheduledThreadPoolExecutor.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return scheduledThreadPoolExecutor.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return scheduledThreadPoolExecutor.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return scheduledThreadPoolExecutor.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return scheduledThreadPoolExecutor.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return scheduledThreadPoolExecutor.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return Collections.emptyList();
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return Collections.emptyList();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return scheduledThreadPoolExecutor.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return scheduledThreadPoolExecutor.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        scheduledThreadPoolExecutor.execute(command);
    }
}
