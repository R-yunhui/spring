package com.ral.young.jvm;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.lang.reflect.Method;

/**
 * 自定义类加载器
 * {@link ClassLoader}
 * 自定义类加载器：主要是实现 java.lang.ClassLoader#findClass(java.lang.String)
 *
 * @author renyunhui
 * @date 2022-07-27 13:46
 * @since 1.0.0
 */
@Slf4j
public class CustomClassLoader extends ClassLoader {

    private final String classPath;

    public CustomClassLoader(String classPath) {
        this.classPath = classPath;
    }

    public static void main(String[] args) throws Exception {
        // 初始化自定义类加载器 - 制定加载根路径
        // 初始化的时候，调用父类的构造器指定父类加载器 - AppClassLoader
        CustomClassLoader customClassLoader = new CustomClassLoader("D:/renyunhui/test");
        // 指定完整的类名
        Class<?> user = customClassLoader.loadClass("com.ral.young.jvm.User");
        Object o = user.newInstance();
        Method say = user.getDeclaredMethod("say");
        say.invoke(o);
        // 由于双亲委派机制，AppClassLoader 的加载路径下也有这个 User.class，则使用的是 AppClassLoader 进行加载，如果删除，则使用自定义类加载器
        log.info("user 使用的类加载器名称:{}", user.getClassLoader().getClass().getName());

        User user1 = new User();
        log.info("user1 使用的类加载器名称:{}", user1.getClass().getClassLoader());

        // 类对象是否一致：类名和包名一致 && 类加载器一致
        log.info("user 和 user1 是否一致 : {}", user1.getClass().equals(user));
    }

    private byte[] loadByte(String name) throws Exception {
        name = name.replaceAll("\\.", "/");
        FileInputStream fis = new FileInputStream(classPath + "/" + name + ".class");
        int len = fis.available();
        byte[] data = new byte[len];
        fis.read(data);
        fis.close();
        return data;
    }

    /**
     * 重写 findClass 方法，加载自定义路径的 class
     *
     * @param name The <a href="#name">binary name</a> of the class
     * @return class
     */
    @Override
    protected Class<?> findClass(String name) {
        try {
            byte[] data = loadByte(name);
            // jvm 真正装载 class 的逻辑
            return defineClass(name, data, 0, data.length);
        } catch (Exception e) {
            log.error("加载 class 失败,errorMsg:{}", e.getMessage(), e);
            throw new RuntimeException();
        }
    }

    /**
     * 重写父类的 loadClass，打破双亲委派机制
     *
     * @param name    The <a href="#name">binary name</a> of the class
     * @param resolve If <tt>true</tt> then resolve the class
     * @return 加载的 class
     * @throws ClassNotFoundException 类不存在的异常
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);

            if (c == null) {
                // If still not found, then invoke findClass in order
                // to find the class.
                long t1 = System.nanoTime();
                // 自己加载自己的类，jvm 核心类由父类加载器去加载（引导类加载器）
                if (name.startsWith("com.ral.young.jvm")) {
                    c = findClass(name);
                } else {
                    c = this.getParent().loadClass(name);
                }

                // this is the defining class loader; record the stats
                sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                sun.misc.PerfCounter.getFindClasses().increment();
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }
}
