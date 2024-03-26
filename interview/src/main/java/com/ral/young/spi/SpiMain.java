package com.ral.young.spi;

import java.util.ServiceLoader;

/**
 * Spi 验证
 *
 * @author renyunhui
 * @date 2024-03-15 9:42
 * @since 1.0.0
 */
public class SpiMain {

    public static void main(String[] args) {
        // Spi 主要是提供了一个可以扩展框架的方式
        // 再 /resources 目录下创建：/META-INF/services/定义的接口全名的文件  文件中定义需要使用的实现类即可
        ServiceLoader<IAnimal> animals = ServiceLoader.load(IAnimal.class);

        for (IAnimal animal : animals) {
            animal.sayName();
        }
    }
}
