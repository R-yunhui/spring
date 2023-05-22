package com.ral.young.practice.basic;

import java.sql.Driver;
import java.util.ServiceLoader;

/**
 * jdk 的 SPI 机制
 *
 * @author renyunhui
 * @date 2023-04-17 10:28
 * @since 1.0.0
 */
public class SpiTest {

    public static void main(String[] args) {
        /*
         * 是JDK内置的一种 服务提供发现机制，可以用来启用框架扩展和替换组件，主要是被框架的开发人员使用，
         * 比如java.sql.Driver接口，其他不同厂商可以针对同一接口做出不同的实现，MySQL和PostgreSQL都有不同的实现提供给用户，
         * 而Java的SPI机制可以为某个接口寻找服务实现。Java中SPI机制主要思想是将装配的控制权移到程序之外，在模块化设计中这个机制尤其重要，其核心思想就是 解耦。
         *
         * 1.通过 jdk 自带的 ServiceLoader 接口加载某个类
         * 2.通过 pom 文件引入了 mysql 的依赖，则 mysql 依赖的 jar 包：\META-INF\services\java.sql.Driver
         * 3.SPI 机制通过此文件加载了对应的类，不同的驱动针对 Driver 实现了不同的类，解耦，在针对不同的配置选择不同的实现类即可
         */

        ServiceLoader<Driver> serviceLoader = ServiceLoader.load(Driver.class);
        for (Driver next : serviceLoader) {
            // com.mysql.cj.jdbc.Driver
            System.out.println("通过 SPI 机制加载的 serviceLoader :" + next.getClass().getName());
        }
    }
}
