package com.ral.young.night.spring.schedule;

import com.ral.young.night.spring.schedule.config.ScheduledConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Spring 定时
 *
 * @author renyunhui
 * @date 2024-06-12 13:49
 * @since 1.0.0
 */
public class ScheduledApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ScheduledConfig.class);

        /*
         * @EnableScheduling 开启定时任务
         * 通过 @Import 引入配置类 - SchedulingConfiguration
         * 配置类注入了：ScheduledAnnotationBeanPostProcessor bean 的后置处理器
         *
         * 通过 ScheduledAnnotationBeanPostProcessor Bean 的后置处理器进行注解的解析和定时任务的执行
         * 本质是调用 ScheduledExecutorService 的 api 进行定时的调度
         */

        applicationContext.refresh();
    }
}
