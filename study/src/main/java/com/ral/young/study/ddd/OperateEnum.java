package com.ral.young.study.ddd;

/**
 * 使用充血模型 - 枚举
 *
 * @author renyunhui
 * @date 2023-11-14 14:11
 * @since 1.0.0
 */
public enum OperateEnum {

    ADDITION("加法", '+') {
        @Override
        public Double operate(double numOne, double numTwo) {
            return numOne + numTwo;
        }
    },

    SUBTRACTION("减法", '-') {
        @Override
        public Double operate(double numOne, double numTwo) {
            return numOne - numTwo;
        }
    },

    MULTIPLICATION("乘法", '*') {
        @Override
        public Double operate(double numOne, double numTwo) {
            return numOne * numTwo;
        }
    },

    DIVISION("除法", '/') {
        @Override
        public Double operate(double numOne, double numTwo) {
            return numOne / numTwo;
        }
    },

    IMPRESSIONS("取模", '%') {
        @Override
        public Double operate(double numOne, double numTwo) {
            return numOne % numTwo;
        }
    },
    ;

    final String value;

    final char operate;

    OperateEnum(String value, char operate) {
        this.value = value;
        this.operate = operate;
    }

    public String getValue() {
        return value;
    }

    public char getOperate() {
        return operate;
    }

    public abstract Double operate(double numOne, double numTwo);
}
