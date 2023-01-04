package com.ral.young.circulardependency;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 测试循环依赖
 *
 * @author renyunhui
 * @date 2023-01-03 14:07
 * @since 1.0.0
 */
public class MainApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);

        // 循环依赖存在动态代理的解决方案：1.@Lazy 注解  2.更换依赖类的加载顺序
        System.out.println("容器启动完成");

        context.close();
    }
}
