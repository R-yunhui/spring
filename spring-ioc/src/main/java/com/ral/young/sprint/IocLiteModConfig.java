package com.ral.young.sprint;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 使用 lite 模式的配置类，不会给当前类创建 cglib 代理对象
 *
 * @author renyunhui
 * @date 2023-05-23 11:10
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class IocLiteModConfig {

    @Bean
    public User user() {
        Tank tank = tank();
        return new User(1L, "lion", tank);
    }


    @Bean
    public Tank tank() {
        // lite 模式下 tank bean 由于被上面的 beanMethod 调用了，则会初始化两次，不会被 cglib 进行代理拦截
        System.out.println("tank bean 初始化");
        return new Tank(1L, "劳斯莱斯");
    }
}
