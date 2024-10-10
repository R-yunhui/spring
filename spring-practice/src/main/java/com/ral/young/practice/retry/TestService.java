package com.ral.young.practice.retry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author renyunhui
 * @description 这是一个TestService类
 * @date 2024-10-10 10-27-55
 * @since 1.0.0
 */
@Component
@Slf4j
public class TestService {

    @Resource
    private RetryTemplate retryTemplate;

    @Retryable(
            // 捕获到这些异常，会进行重试
            include = {ArrayIndexOutOfBoundsException.class, ClassCastException.class},
            // 不处理这些异常
            exclude = {NullPointerException.class},
            // 最大重试次数
            maxAttempts = 3,
            // 重试的配置，延迟的时间以及对应的倍数，2S 重试一次
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void testDo(int code) throws InterruptedException {
        test(code);
    }

    public void testDoTwo(int code) throws Throwable {
        log.info("testDoTwo");
        // 相比于注解的方式更加灵活，控制的粒度更低，但是需要自己手动捕获异常
        // 不需要考虑 aop 的不适用性
        retryTemplate.execute((RetryCallback<Object, Throwable>) context -> {
            test(code);
            return null;
        }, context -> {
            testDoError(code);
            return null;
        });
    }

    @Recover
    public void testDoError(int code) {
        // 补偿措施的方法需要和目标方法的参数和返回值一致
        log.warn("重试多次之后依然失败，进行补偿措施，code：{}", code);
    }


    public static void test(int code) throws InterruptedException {
        switch (code) {
            case 1:
                Thread.sleep(1000L);
                log.error("数组下标越界异常，无法处理");
                throw new ArrayIndexOutOfBoundsException();
            case 2:
                Thread.sleep(2000L);
                log.error("类型转换异常，无法处理");
                throw new ClassCastException();
            case 3:
                Thread.sleep(3000L);
                log.error("空指针异常，无法处理");
                throw new NullPointerException();
            default:
                Thread.sleep(500L);
                log.info("正常情况，执行完成");
                break;
        }
    }
}
