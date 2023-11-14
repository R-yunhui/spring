package com.ral.young.study.designpattern.statepattern.enumstate;

/**
 * 订单状态枚举
 *
 * @author renyunhui
 * @date 2023-11-14 15:57
 * @since 1.0.0
 */
public enum OrderStateEnum {

    DISPATCH("调度中") {
        @Override
        public OrderStateEnum nextState() {
            return DELIVERY;
        }

        @Override
        public OrderStateEnum preState() {
            return this;
        }
    },

    DELIVERY("派送中") {
        @Override
        public OrderStateEnum nextState() {
            return RECEIPT;
        }

        @Override
        public OrderStateEnum preState() {
            return DISPATCH;
        }
    },

    RECEIPT("收货") {
        @Override
        public OrderStateEnum nextState() {
            return this;
        }

        @Override
        public OrderStateEnum preState() {
            return DELIVERY;
        }
    }
    ;

    private final String desc;

    private OrderStateEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 下一个状态
     * @return 下一个状态
     */
    public abstract OrderStateEnum nextState();

    /**
     * 前一个状态
     * @return 前一个状态
     */
    public abstract OrderStateEnum preState();
    }
