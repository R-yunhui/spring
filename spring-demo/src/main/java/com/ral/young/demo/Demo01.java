package com.ral.young.demo;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author renyunhui
 * @description 这是一个Demo01类
 * @date 2024-09-11 15-47-27
 * @since 1.0.0
 */
@Slf4j
public class Demo01 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // testOne();

        testTwo();
    }

    private static void testTwo() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return test();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).whenComplete((r, t) -> {
            if (null != t) {
                log.error("执行失败", t);
            } else {
                log.info("异步执行结果：{}", r);
            }
        });

        log.info("testTwo 主程序执行完成");
        String s = completableFuture.get();
        log.info("testTwo 获取子线程执行结果：{}", s);
    }

    private static void testOne() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = CompletableFuture.completedFuture(test()).whenComplete((r, t) -> {
            if (null != t) {
                log.error("执行失败", t);
            } else {
                log.info("执行结果：{}", r);
            }
        });

        log.info("主程序执行完成");
        String s = completableFuture.get();
        log.info("获取子线程执行结果：{}", s);
    }

    private static String test() throws InterruptedException {
        log.info("开始执行");
        Thread.sleep(5000);
        log.info("执行结束");
        return "success";
    }

    public String testAsync() throws InterruptedException {
        log.info("开始执行");
        Thread.sleep(5000);
        log.info("执行结束");
        return "success";
    }
}
