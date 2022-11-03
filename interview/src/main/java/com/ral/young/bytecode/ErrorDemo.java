package com.ral.young.bytecode;

/**
 * java 虚拟机的异常处理
 *
 * @author renyunhui
 * @date 2022-11-02 10:31
 * @since 1.0.0
 */
public class ErrorDemo {

    public static void main(String[] args) {
        int num = 0;
        int errorNum = 0;
        int finallyNum = 0;
        try {
            num = 2;
        } catch (Exception e) {
            errorNum = 3;
            throw new RuntimeException("异常信息");
        } finally {
            finallyNum = 4;
        }
    }
}
