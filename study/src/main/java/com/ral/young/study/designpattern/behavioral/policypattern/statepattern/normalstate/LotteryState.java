package com.ral.young.study.designpattern.behavioral.policypattern.statepattern.normalstate;

/**
 * 定义一个抽奖状态的接口 - state
 * 定义每个状态的行为
 *
 * @author renyunhui
 * @date 2023-11-14 10:18
 * @since 1.0.0
 */
public interface LotteryState {

    /**
     * 扣除积分
     */
    void deductedPoint();

    /**
     * 是否中奖
     * @return 是否中奖
     */
    boolean raffle();

    /**
     * 方法奖品
     */
    void distributedPrize();
}
