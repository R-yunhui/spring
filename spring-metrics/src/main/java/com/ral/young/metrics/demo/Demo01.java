package com.ral.young.metrics.demo;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

/**
 * @author renyunhui
 * @description 这是一个Demo01类
 * @date 2024-12-03 14-32-34
 * @since 1.0.0
 */
@Slf4j
public class Demo01 {

    public static void main(String[] args) {
        List<String> rules = new ArrayList<>();
        List<CompletableFuture<Boolean>> completableFutures = new ArrayList<>();
        IntStream.range(1, 100).forEach(i -> rules.add(RandomUtil.randomString(4)));
        for (String rule : rules) {
            CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(() -> {
                // 模拟任务的执行
                try {
                    log.info("{} 正在执行规则: {} 的校验", Thread.currentThread().getName(), rule);
                    int random = RandomUtil.randomInt(5, 10);

                    // 模拟任务执行的耗时
                    Thread.sleep(random * 1000L);

                    int result = RandomUtil.randomInt(1, 10);
                    log.info("{} 执行规则完毕: {} 结果: {}", Thread.currentThread().getName(), rule, result);
                    return result >= 5;
                } catch (Exception e) {
                    log.error("{} 规则: {}, 执行失败", Thread.currentThread().getName(), rule, e);
                    return false;
                }
            });
            completableFutures.add(completableFuture);
        }

        // 只要有一个规则通过，那么后续的所有都不会再执行
        CompletableFuture<Object> future = CompletableFuture.anyOf(completableFutures.toArray(new CompletableFuture[0]));
        try {
            Boolean result = (Boolean) future.get();
            log.info("任务执行完毕：{}", result);
        } catch (Exception e) {
            log.error("任务执行失败", e);
        }
    }

}
