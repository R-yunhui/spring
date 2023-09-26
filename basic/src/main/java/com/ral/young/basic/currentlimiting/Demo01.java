package com.ral.young.basic.currentlimiting;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 滑动窗口算法
 *
 * @author renyunhui
 * @date 2023-09-26 9:47
 * @since 1.0.0
 */
public class Demo01 {

    /**
     * 单位时间划分的小周期（单位时间是1分钟，10s一个小格子窗口，一共6个格子）
     */
    private final int SUB_CYCLE = 10;

    /**
     * 每分钟限流请求数
     */
    private final int THRESHOLD_PER_MIN = 100;

    /**
     * 计数器, k-为当前窗口的开始时间值秒，value为当前窗口的计数
     * 默认按照 key 的自然顺序排序
     */
    private final TreeMap<Long, Integer> counters = new TreeMap<>();

    public static void main(String[] args) {

    }

    private boolean slidingWindowsTryAcquire() {
        long currentWindowTime = System.currentTimeMillis();
        // 当前窗口总请求数
        int currentWindowNum = countCurrentWindow(currentWindowTime);

        // 超过阀值限流
        if (currentWindowNum >= THRESHOLD_PER_MIN) {
            return false;
        }

        // 计数器+1
        counters.put(currentWindowTime, counters.get(currentWindowTime) + 1);
        return true;
    }

    private int countCurrentWindow(long currentWindowTime) {
        // 计算窗口开始位置
        long startTime = currentWindowTime - SUB_CYCLE * (60 / SUB_CYCLE - 1);
        int count = 0;

        // 遍历存储的计数器
        Iterator<Map.Entry<Long, Integer>> iterator = counters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Integer> entry = iterator.next();
            // 删除无效过期的子窗口计数器
            if (entry.getKey() < startTime) {
                iterator.remove();
            } else {
                // 累加当前窗口的所有计数器之和
                count = count + entry.getValue();
            }
        }
        return count;
    }
}
