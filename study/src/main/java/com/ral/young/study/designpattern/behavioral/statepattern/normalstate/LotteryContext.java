package com.ral.young.study.designpattern.behavioral.statepattern.normalstate;

/**
 * 抽奖状态的 context
 * 定义了客户端需要的接口，内部维护一个当前状态，并负责具体状态的切换
 *
 * @author renyunhui
 * @date 2023-11-14 10:22
 * @since 1.0.0
 */
public class LotteryContext {

    /**
     * 当前上下文的状态
     */
    private LotteryState state;

    /**
     * 奖品数量
     */
    private int prizeCount;

    private CanLotteryState canLotteryState = new CanLotteryState(this);

    private NoLotteryState noLotteryState = new NoLotteryState(this);

    private WinningState winningState = new WinningState(this);

    private NoPrizeState noPrizeState = new NoPrizeState(this);

    public void lottery() {
        // 扣减积分
        state.deductedPoint();

        if (state.raffle()) {
            // 如果抽到奖品,则获得即可
            state.distributedPrize();
        }
        System.out.println();
    }

    public LotteryContext(int prizeCount) {
        this.prizeCount = prizeCount;
        // 默认是不能抽奖的状态
        this.state = getNoLotteryState();
    }

    public LotteryState getState() {
        return state;
    }

    public void setState(LotteryState state) {
        this.state = state;
    }

    public int getPrizeCount() {
        return prizeCount;
    }

    public void setPrizeCount(int prizeCount) {
        this.prizeCount = prizeCount;
    }

    public CanLotteryState getCanLotteryState() {
        return canLotteryState;
    }

    public NoLotteryState getNoLotteryState() {
        return noLotteryState;
    }

    public WinningState getWinningState() {
        return winningState;
    }

    public NoPrizeState getNoPrizeState() {
        return noPrizeState;
    }
}
