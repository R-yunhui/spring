package com.ral.young.study.designpattern.behavioral.policypattern.statepattern.normalstate;

/**
 * 没有奖品的状态
 *
 * @author renyunhui
 * @date 2023-11-14 10:33
 * @since 1.0.0
 */
public class NoPrizeState implements LotteryState {

    private LotteryContext lotteryContext;

    public NoPrizeState(LotteryContext lotteryContext) {
        this.lotteryContext = lotteryContext;
    }

    @Override
    public void deductedPoint() {
        System.err.println("积分已经扣除,无需重复扣减");
    }

    @Override
    public boolean raffle() {
        System.err.println("当前状态不允许抽奖");
        return false;
    }

    @Override
    public void distributedPrize() {
        System.out.println("奖品发送完毕,很遗憾");
    }
}
