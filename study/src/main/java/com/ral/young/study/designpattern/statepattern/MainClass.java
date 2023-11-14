package com.ral.young.study.designpattern.statepattern;

/**
 * main 方法测试
 *
 * @author renyunhui
 * @date 2023-11-14 10:43
 * @since 1.0.0
 */
public class MainClass {

    public static void main(String[] args) {
        // 设定一个奖品
        LotteryContext lotteryContext = new LotteryContext(1);

        // 模拟抽奖 30 次
        for (int i = 0, n = 30; i < n; i++){
            lotteryContext.lottery();
        }
    }
}
