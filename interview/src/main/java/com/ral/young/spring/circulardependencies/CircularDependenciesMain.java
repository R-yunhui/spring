package com.ral.young.spring.circulardependencies;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 复现单例代理对象的循环依赖问题
 *
 * @author renyunhui
 * @date 2023-02-16 19:44
 * @since 1.0.0
 */
public class CircularDependenciesMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(CircularDependenciesConfig.class);

        /*
         * 解决依赖注入的方式：
         * 1.调换类的加载顺序（修改类文件 or @DependsOn）
         * 2.使用 @Lazy 注解
         */

        applicationContext.close();
    }
}
