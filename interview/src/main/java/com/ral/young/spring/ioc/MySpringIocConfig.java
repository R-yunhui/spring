package com.ral.young.spring.ioc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author renyunhui
 * @date 2023-02-16 14:28
 * @since 1.0.0
 */
@Configuration
@ComponentScan(value = "com.ral.young.spring.ioc")
public class MySpringIocConfig {

    @Bean(initMethod = "initTwo", destroyMethod = "destroyTwo")
    public User user() {
        // full 类型的
        // 标记了 @Configuration 默认 boolean proxyBeanMethods() default true;
        return new User("ryh", 25);
    }

    @Bean
    public Student student() {
        // full 类型的
        // 标记了 @Configuration 默认 boolean proxyBeanMethods() default true;

        // 标记了 @Configuration，再次调用 this.user(); 会被其被 Spring 创建的 cglib 代理对象所拦截，通过 BeanFactory 进行获取，而不是再次创建
        User user = this.user();
        return new Student(1, "ryh", user);
    }
}
