package com.ral.young.study.ddd;

/**
 *
 * @author renyunhui
 * @date 2023-11-14 14:33
 * @since 1.0.0
 */
public class MainClass {

    public static void main(String[] args) {
        // 使用充血模型的枚举模拟计算器操作
        double numOne = 2.5;
        double numTwo = 2.7;
        System.out.println(OperateEnum.ADDITION.value + ":" + compute(OperateEnum.ADDITION, numOne, numTwo));
        System.out.println(OperateEnum.SUBTRACTION.value + ":" + compute(OperateEnum.SUBTRACTION, numOne, numTwo));
        System.out.println(OperateEnum.MULTIPLICATION.value + ":" + compute(OperateEnum.MULTIPLICATION, numOne, numTwo));
        System.out.println(OperateEnum.DIVISION.value + ":" + compute(OperateEnum.DIVISION, numOne, numTwo));
        System.out.println(OperateEnum.IMPRESSIONS.value + ":" + compute(OperateEnum.IMPRESSIONS, numOne, numTwo));
    }

    private static double compute(OperateEnum operateEnum, double numOne, double numTwo) {
        return operateEnum.operate(numOne, numTwo);
    }
}
