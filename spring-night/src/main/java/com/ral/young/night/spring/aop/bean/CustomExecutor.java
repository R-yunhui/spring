package com.ral.young.night.spring.aop.bean;

import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义 @Async 注解使用的线程池
 *
 * @author renyunhui
 * @date 2024-06-12 16:42
 * @since 1.0.0
 */
@Component(value = "CustomExecutor")
public class CustomExecutor implements AsyncListenableTaskExecutor {

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10));


    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        ListenableFuture<?> future = new ListenableFutureTask<>(task, null);
        executor.execute(task);
        return future;
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        ListenableFuture<T> future = new ListenableFutureTask<>(task);
        executor.submit(task);
        return future;
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        executor.execute(task);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return executor.submit(task);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }

    @Override
    public void execute(Runnable task) {
        executor.execute(task);
    }
}
