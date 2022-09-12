package com.ral.young.concurrent.basic;

import cn.hutool.core.date.StopWatch;

import java.util.concurrent.TimeUnit;

/**
 * cpu 基础
 *
 * @author renyunhui
 * @date 2022-09-12 15:32
 * @since 1.0.0
 */
public class CpuBasic {

    public static void main(String[] args) {
        int logicCore = Runtime.getRuntime().availableProcessors();
        System.out.println("当前机器的逻辑核数（逻辑处理器）为：" + logicCore);

        long totalMemory = Runtime.getRuntime().totalMemory();
        System.out.println("当前虚拟机的内存总量：" + totalMemory);

        /*
         * 模拟 cpu 的局部性原则：空间局部性
         * 如果一个存储器的位置被引用，那么将来他附近的位置也会被引用。（将它临近的区域的信息都读进去）
         */
        testSpatialLocality();
    }

    private static void testSpatialLocality() {
        StopWatch stopWatch = new StopWatch("CPU 空间局部性原则验证");
        // 初始化一个二维数组
        stopWatch.start("初始化二维数组");
        int size = 10240;
        long[][] arr = new long[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                arr[i][j] = i;
            }
        }
        stopWatch.stop();

        // 按行遍历二维数组
        long sum = 0;
        stopWatch.start("按行遍历二维数组");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sum += arr[i][j];
            }
        }
        stopWatch.stop();
        System.out.println("按行遍历二维数组 sum：" + sum);

        // 按列遍历二维数组
        sum = 0;
        stopWatch.start("按列遍历二维数组");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sum += arr[j][i];
            }
        }
        stopWatch.stop();
        System.out.println("按列遍历二维数组 sum：" + sum);
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
    }
}
