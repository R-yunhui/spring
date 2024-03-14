package com.ral.young.concurrent;

import cn.hutool.core.date.StopWatch;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.concurrent.TimeUnit;

/**
 * Cpu 基础
 *
 * @author renyunhui
 * @date 2024-03-12 9:45
 * @since 1.0.0
 */
public class CpuBasic {

    public static void main(String[] args) {
        // 打印一些 cpu 的基础信息
        printCpuBasicInfo();

        // 测试空间局部性原则
        testSpatialLocality();
    }

    public static void printCpuBasicInfo() {
        // 获取当前 Cpu 和逻辑核心线程数
        int coreSize = Runtime.getRuntime().availableProcessors();
        System.out.println("当前 Cpu 和逻辑核心线程数：" + coreSize);

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

        // 椎内存使用情况
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();

        // 初始的总内存
        long totalMemorySize = memoryUsage.getInit();
        System.out.println("初始的总内存：" + (totalMemorySize / 1024 / 1024 + "M"));

        // 最大可用内存
        long maxMemorySize = memoryUsage.getMax();
        System.out.println("最大可用内存：" + (maxMemorySize / 1024 / 1024 + "M"));

        // 已使用的内存
        long usedMemorySize = memoryUsage.getUsed();
        System.out.println("已使用的内存：" + usedMemorySize / 1024 / 1024 + "M");
    }

    public static void testSpatialLocality() {
        // cpu 存在三级缓存，默认使用 cacheLine 运输数据，64byte 大小
        int m = 10240;
        int n = 10240;
        int[][] arr = new int[m][n];

        // 初始化
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                arr[i][j] = 1;
            }
        }

        StopWatch stopWatch = new StopWatch("空间局部性原则测试");
        stopWatch.start("按行遍历二维数组");
        long ansOne = 0;
        // 按行遍历
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                ansOne += arr[i][j];
            }
        }
        System.out.println("按行遍历二维数组结果：" + ansOne);
        stopWatch.stop();

        // 按列遍历
        stopWatch.start("按列遍历二维数组");
        long ansTwo = 0;
        // 按行遍历
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                ansTwo += arr[j][i];
            }
        }
        System.out.println("按列遍历二维数组结果：" + ansTwo);
        stopWatch.stop();

        // 由于空间局部性原则：如果一个存储器的位置被引用，那么将来他附近的位置也会被引用。（将它临近的区域的信息都读进去）比如顺序执行的代码、连续创建的两个对象、数组等。
        // 按列遍历不能利用到空间局部性原则，它跨越了数组，利用不到 cacheLine 一次获取同一个数组的多个元素
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
    }
}
