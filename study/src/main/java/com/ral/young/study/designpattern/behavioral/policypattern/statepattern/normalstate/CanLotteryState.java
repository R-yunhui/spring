package com.ral.young.study.designpattern.behavioral.policypattern.statepattern.normalstate;

import java.util.Random;

/**
 * 定义的状态具体实现类
 * 允许抽奖
 *
 * @author renyunhui
 * @date 2023-11-14 10:21
 * @since 1.0.0
 */
public class CanLotteryState implements LotteryState {

    private static final Random RANDOM = new Random(10);

    private LotteryContext lotteryContext;

    public CanLotteryState(LotteryContext lotteryContext) {
        this.lotteryContext = lotteryContext;
    }

    @Override
    public void deductedPoint() {
        System.err.println("积分已经扣除,无需重复扣减");
    }

    @Override
    public boolean raffle() {
        System.out.println("准备开始抽奖,请稍等.....");
        int num = RANDOM.nextInt();
        if (num < 2) {
            // 10% 的中奖概率
            lotteryContext.setState(lotteryContext.getWinningState());
            return true;
        }

        // 没中奖,则返回不允许抽奖的状态
        System.out.println("很遗憾没有抽中奖品");
        lotteryContext.setState(lotteryContext.getNoLotteryState());
        return false;
    }

    @Override
    public void distributedPrize() {
        System.err.println("还未中奖,不允许发放奖品");
    }
}
