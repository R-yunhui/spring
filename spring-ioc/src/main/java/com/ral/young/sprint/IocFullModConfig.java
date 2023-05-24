package com.ral.young.sprint;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 使用 full 模式的配置类，会给当前类创建 cglib 代理对象，拦截 beanMethod
 *
 * @author renyunhui
 * @date 2023-05-23 11:08
 * @since 1.0.0
 */
@Configuration
public class IocFullModConfig {

    @Bean
    public Animal animal() {
        return new Animal(1L, "lion", car());
    }

    @Bean
    public Car car() {
        // full 模式下 car bean 只会被初始化一次，上面的 beanMethod 调用会被 cglib 进行拦截，判断 beanFactory 中是否存在，存在则直接从 beanFactory 中获取
        System.out.println("car bean 初始化");
        return new Car(1L, "劳斯莱斯");
    }
}
