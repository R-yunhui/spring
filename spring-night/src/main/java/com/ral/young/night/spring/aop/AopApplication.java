package com.ral.young.night.spring.aop;

import com.ral.young.night.spring.aop.config.AopConfig;
import com.ral.young.night.spring.aop.service.TestService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.stream.IntStream;

/**
 * Aop Application
 *
 * @author renyunhui
 * @date 2024-06-07 15:52
 * @since 1.0.0
 */
public class AopApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

        /*
         *  Spring 事务失效（依赖 SpringAop - 动态代理）
         * 1.@Transactional 注解应用在非 public 方法上
         * 2.方法在同一个类的内部调用
         * 3.@Transactional 注解应用在 final or static 方法上
         * 4.事务传播级别配置错误
         * 5.rollbackFor 设置错误
         * 6.异常被捕获为对外进行抛出
         * 7.数据库不支持事务
         */

        /*
         * @EnableAsync 开启异步注解 - AsyncConfigurationSelector - ProxyAsyncConfiguration
         * @Async 注解 - AsyncExecutionInterceptor
         * 如果不进行指定线程池，那么会默认使用 SimpleAsyncTaskExecutor，它并不是一个真正意义的线程池
         * 不会重用线程，而是每次调用都会创建一个新的线程去执行任务，高并发会带来性能问题
         *
         * 1.通过实现 TaskExecutor 接口自定义线程池，默认线程池就会换成这个
         * 2.@Async(value = "xxx") 指定各自的线程池名称，会通过 beanFactory 去找对应的线程池 bean，这个 bean 需要实现 Executor（优先级 > 1）
         */
        applicationContext.register(AopConfig.class);
        applicationContext.refresh();

        TestService testService = applicationContext.getBean(TestService.class);
        IntStream.range(0, 10).forEach(i -> {
            testService.testOne();
        });

        applicationContext.close();
    }
}
