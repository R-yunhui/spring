package com.ral.young.study.designpattern.behavioral.statepattern.normalstate;

/**
 * 无法抽奖的状态
 *
 * @author renyunhui
 * @date 2023-11-14 10:31
 * @since 1.0.0
 */
public class NoLotteryState implements LotteryState {

    private LotteryContext lotteryContext;

    public NoLotteryState(LotteryContext lotteryContext) {
        this.lotteryContext = lotteryContext;
    }

    @Override
    public void deductedPoint() {
        System.out.println("积分扣除成功,准备开始抽奖");
        lotteryContext.setState(lotteryContext.getCanLotteryState());
    }

    @Override
    public boolean raffle() {
        System.err.println("需要先扣除积分,才允许抽奖");
        return false;
    }

    @Override
    public void distributedPrize() {
        System.err.println("需要先中奖,才能发奖品");
    }
}
