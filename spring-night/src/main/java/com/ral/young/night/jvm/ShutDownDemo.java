package com.ral.young.night.jvm;

/**
 * kill -9
 * kill -15
 *
 * @author renyunhui
 * @date 2024-06-05 15:18
 * @since 1.0.0
 */
public class ShutDownDemo {

    public static void main(String[] args) {
        /*
         * kill -9 强制终止，程序没有时间进行准备工作，可能会带来数据等
         * kill -15 通知程序进行清理工作，程序会进行一些清理工作，比如保存数据等
         */
        testKill15();
    }

    private static void testKill15() {
        boolean flag = true;

        // 自定义jvm 清理动作，依赖 JDK 提供的 shutdown 的 Hook 实现
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // kill 15 的方式会回调这个 Hook
            System.out.println("shutdown hook execute");
        }));

        while (flag) {

        }

        System.out.println("main thread execute end");
    }
}
