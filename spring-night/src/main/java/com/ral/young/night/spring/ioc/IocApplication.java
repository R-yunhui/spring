package com.ral.young.night.spring.ioc;

import com.ral.young.night.spring.ioc.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.EventListener;

import java.util.Map;

/**
 * Spring IOC
 *
 * @author renyunhui
 * @date 2024-06-07 14:37
 * @since 1.0.0
 */
@Slf4j
public class IocApplication {

    public static void main(String[] args) {
        /*
         * SpringIOC: 将创建对象的权力交给容器，应用程序不在控制对象的创建而是被动的接受由容器创建的对象
         *
         * 好处：
         * 1.使用者不需要关心引用 bean 的实现细节
         * 2.不用创建多个相同的 bean 导致浪费
         * 3.Bean 的修改使用方无需感知
         */
        // 初始化Spring环境上下文
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 扫描配置
        applicationContext.scan("com.ral.young.night.spring.ioc.config");
        // 核心方法，刷新上下文
        applicationContext.refresh();

        /*
         * Bean 的生命周期
         * 1.实例化 Bean
         * 2.设置属性值
         * 3.检查回调 Aware
         * 4.调用 BeanPostProcessor 的前置处理方法（@PostConstruct - CommonAnnotationBeanPostProcessor）
         * 5.调用 InitializingBean afterPropertiesSet() 方法
         * 6.调用自定义的 init-method 方法
         * 7.调用 BeanPostProcessor 的后置处理方法
         * 8.注册 Destruction 的回调
         * 9.Bean 准备就绪
         * 10.调用 DisposeBean 的 destroy() 方法
         * 11.调用自定义的 destroy-method 方法
         *
         */
        User bean = applicationContext.getBean(User.class);
        log.info("从容器中获取到的 User 实例：{}", bean);

        Map<String, Object> beansWithEventListener = applicationContext.getBeansWithAnnotation(EventListener.class);
        log.info("容器中所有被 @EventListener 注解的 Bean：{}", beansWithEventListener);

        // 关闭容器
        applicationContext.close();
    }
}
