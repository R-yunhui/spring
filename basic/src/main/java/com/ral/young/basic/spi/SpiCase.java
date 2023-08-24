package com.ral.young.basic.spi;

import java.util.ServiceLoader;

/**
 * SPI（Service Provider Interface），是JDK内置的一种 服务提供发现机制，可以用来启用框架扩展和替换组件，
 * 主要是被框架的开发人员使用，比如java.sql.Driver接口，其他不同厂商可以针对同一接口做出不同的实现，MySQL和PostgreSQL都有不同的实现提供给用户，
 * 而Java的SPI机制可以为某个接口寻找服务实现。Java中SPI机制主要思想是将装配的控制权移到程序之外，在模块化设计中这个机制尤其重要，其核心思想就是 解耦。
 *
 * @author renyunhui
 * @date 2023-07-05 10:29
 * @since 1.0.0
 */
public class SpiCase {

    public static void main(String[] args) {
        // 通过 jdk 内置的 ServiceLoader
        ServiceLoader<Search> serviceLoader = ServiceLoader.load(Search.class);
        // resources下新建 META-INF/services/目录，然后新建接口全限定名的文件：com.ral.young.basic.spi.com.ral.young.basic.spi.search;，里面加上我们需要用到的实现类
        for (Search search : serviceLoader) {
            // 配置了多个就会存在多个接口的实现
            search.searchDoc("test");
        }
    }
}
