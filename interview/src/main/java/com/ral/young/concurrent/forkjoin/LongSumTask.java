package com.ral.young.concurrent.forkjoin;

import java.util.Arrays;
import java.util.concurrent.RecursiveTask;

/**
 * {@link RecursiveTask}
 * {@link java.util.concurrent.ForkJoinTask}
 * 封装的一个异步任务 - forkJoinTask ，计算大数求和
 *
 * @author renyunhui
 * @date 2023-02-14 10:00
 * @since 1.0.0
 */
public class LongSumTask extends RecursiveTask<Long> {

    /**
     * 任务拆分的最小阈值
     */
    static final int SEQUENTIAL_THRESHOLD = 1000;

    /**
     * 左边界
     */
    int low;

    /**
     * 右边界
     */
    int high;

    /**
     * 数组内容
     */
    int[] array;

    public LongSumTask(int low, int high, int[] array) {
        this.low = low;
        this.high = high;
        this.array = array;
    }

    protected Long compute() {
        // 任务被拆分到足够小时，则开始求和
        if (high - low <= SEQUENTIAL_THRESHOLD) {
            long sum = 0;
            for (int i = low; i < high; ++i) {
                sum += array[i];
            }
            return sum;
        } else {
            // 如果任务任然过大，则继续拆分任务，本质就是递归拆分
            int mid = low + (high - low) / 2;
            LongSumTask left = new LongSumTask(low, mid, array);
            LongSumTask right = new LongSumTask(mid, high, array);
            // 任务拆分
            left.fork();
            right.fork();

            // 任务合并
            long rightAns = right.join();
            long leftAns = left.join();
            return leftAns + rightAns;
        }
    }
}
