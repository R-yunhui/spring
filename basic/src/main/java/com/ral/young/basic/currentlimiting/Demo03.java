package com.ral.young.basic.currentlimiting;

/**
 * 令牌桶算法
 *
 * @author renyunhui
 * @date 2023-09-26 10:12
 * @since 1.0.0
 */
public class Demo03 {

    private final int capacity;     // 令牌桶容量
    private final int rate;         // 令牌生成速率，单位：令牌/秒
    private int tokens;             // 当前令牌数量
    private long lastRefillTimestamp;  // 上次令牌生成时间戳

    /**
     * 构造函数中传入令牌桶的容量和令牌生成速率。
     *
     * @param capacity 令牌桶容量
     * @param rate     令牌生成速率，单位：令牌/秒
     */
    public Demo03(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
        this.tokens = capacity;
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    /**
     * allowRequest() 方法表示一个请求是否允许通过，该方法使用 synchronized 关键字进行同步，以保证线程安全。
     *
     * @return 请求是否允许执行
     */
    public synchronized boolean allowRequest() {
        refill();
        if (tokens > 0) {
            tokens--;
            return true;
        }

        return false;
    }

    /**
     * refill() 方法用于生成令牌，其中计算令牌数量的逻辑是按照令牌生成速率每秒钟生成一定数量的令牌，
     * tokens 变量表示当前令牌数量，
     * lastRefillTimestamp 变量表示上次令牌生成的时间戳。
     */
    private void refill() {
        long now = System.currentTimeMillis();
        if (now > lastRefillTimestamp) {
            // 生成的令牌数量
            int generatedTokens = (int) ((now - lastRefillTimestamp) / 1000 * rate);
            tokens = Math.min(tokens + generatedTokens, capacity);
            lastRefillTimestamp = now;
        }
    }
}
