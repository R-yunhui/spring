package com.ral.young;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author renyunhui
 * @description 这是一个Main类
 * @date 2024-09-30 13-55-48
 * @since 1.0.0
 */
@Slf4j
public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
                log.info("任务1执行完毕:{}", Thread.currentThread().getId());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(3000);
                log.info("任务2执行完毕:{}", Thread.currentThread().getId());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(5000);
                log.info("任务3执行完毕:{}", Thread.currentThread().getId());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<Void> future4 = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(7000);
                log.info("任务4执行完毕:{}", Thread.currentThread().getId());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        completableFutures.add(future1);
        completableFutures.add(future2);
        completableFutures.add(future3);
        completableFutures.add(future4);

        CompletableFuture<Void> future = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]));
        future.get();

        log.info("任务执行完毕：{}", Thread.currentThread().getId());
    }
}
