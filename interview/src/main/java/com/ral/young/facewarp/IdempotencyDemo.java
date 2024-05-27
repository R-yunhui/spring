package com.ral.young.facewarp;

/**
 * 如何保证幂等性
 *
 * @author renyunhui
 * @date 2024-05-22 10:53
 * @since 1.0.0
 */
public class IdempotencyDemo {

    public static void main(String[] args) {

        /*
         * 幂等性：分为三步
         *  1.加锁，互斥锁，可以使用 redis 的分布式锁
         *  2.判断，判断是否存在重复，依赖流水表，唯一索引，状态机等
         *  3.更新，保证加锁成功的前提进行数据的更新及持久化
         */


    }
}
