package com.ral.young.bytecode;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * jvm 如何处理异常
 *
 * @author renyunhui
 * @date 2024-03-12 9:22
 * @since 1.0.0
 */
public class JvmExceptionDemo {

    public static void main(String[] args) {
        JvmExceptionDemo demo = new JvmExceptionDemo();
        demo.testDealException();
    }

    public void testDealException() {
        try {
            int i = 1 / 0;
            FileInputStream fileInputStream = new FileInputStream("");
            fileInputStream.read();
        } catch (IOException e1) {
            System.out.println(e1.getMessage());
        } catch (Exception e2) {
            System.out.println(e2.getMessage());
        } finally {
            System.out.println("deal over");
        }
    }
}
