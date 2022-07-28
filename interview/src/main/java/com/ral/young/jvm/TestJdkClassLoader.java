package com.ral.young.jvm;

import lombok.extern.slf4j.Slf4j;
import sun.misc.Launcher;

import java.net.URL;

/**
 * jvm 的 classLoader
 *
 * @author renyunhui
 * @date 2022-07-27 10:42
 * @since 1.0.0
 */
@Slf4j
public class TestJdkClassLoader {

    public static void main(String[] args) {
        log.info("java.lang.String 的类加载器 : {}", String.class.getClassLoader());
        log.info("com.sun.crypto.provider.DESedeCipher 的类加载器 : {}", com.sun.crypto.provider.DESedeCipher.class.getClassLoader());
        log.info("TestJdkClassLoader 的类加载器 : {}", TestJdkClassLoader.class.getClassLoader());

        System.out.println();
        // 应用程序类加载器
        ClassLoader appClassLoader = TestJdkClassLoader.class.getClassLoader();
        // 扩展类加载器
        ClassLoader extClassLoader = appClassLoader.getParent();
        // 引导类加载器 c++对象
        ClassLoader bootstrapClassLoader = extClassLoader.getParent();

        log.info("appClassLoader : {}", appClassLoader);
        log.info("extClassLoader : {}", extClassLoader);
        log.info("bootstrapClassLoader : {}", bootstrapClassLoader);

        System.out.println();
        log.info("bootstrapClassLoader 的加载路径如下：");
        URL[] urls = Launcher.getBootstrapClassPath().getURLs();
        for (URL url : urls) {
            log.info("路径 : {} ", url);
        }

        System.out.println();
        log.info("extClassloader 的加载路径如下：");
        log.info(System.getProperty("java.ext.dirs"));

        System.out.println();
        log.info("appClassLoader 的加载路径如下：");
        log.info(System.getProperty("java.class.path"));
    }
}
