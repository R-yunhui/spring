package com.ral.young.spring.ioc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author renyunhui
 * @date 2023-02-16 14:31
 * @since 1.0.0
 */
@Slf4j
public class IocMainStart {

    public static void main(String[] args) {
        // 创建一个 ApplicationContext
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MySpringIocConfig.class);

        // 通过 @Bean 注解，直接添加一个 Bean 到 BeanFactory 中
        User bean = applicationContext.getBean(User.class);
        log.info("获取到的 bean 对象:{}", bean);

        Student bean2 = applicationContext.getBean(Student.class);
        log.info("获取到的 bean 对象:{}", bean2);

        applicationContext.close();
    }
}
