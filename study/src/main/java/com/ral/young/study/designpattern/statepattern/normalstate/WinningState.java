package com.ral.young.study.designpattern.statepattern.normalstate;

/**
 * 中奖状态
 *
 * @author renyunhui
 * @date 2023-11-14 10:32
 * @since 1.0.0
 */
public class WinningState implements LotteryState {

    private LotteryContext lotteryContext;

    public WinningState(LotteryContext lotteryContext) {
        this.lotteryContext = lotteryContext;
    }

    @Override
    public void deductedPoint() {
        System.err.println("积分已经扣除,无需重复扣减");
    }

    @Override
    public boolean raffle() {
        System.err.println("抽奖已经结束");
        return false;
    }

    @Override
    public void distributedPrize() {
        if (lotteryContext.getPrizeCount() > 0) {
            System.out.println("开始发放奖品");
            lotteryContext.setPrizeCount(lotteryContext.getPrizeCount() - 1);
            lotteryContext.setState(lotteryContext.getNoLotteryState());
        } else {
            System.out.println("很遗憾,奖品已经发送完毕");
            lotteryContext.setState(lotteryContext.getNoPrizeState());
        }
    }
}
