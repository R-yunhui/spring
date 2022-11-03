package com.ral.young.concurrent.lock;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * 栈内分配 - 示例
 * 打开逃逸分析：-XX:+DoEscapeAnalysis
 *
 * @author renyunhui
 * @date 2022-09-15 15:46
 * @since 1.0.0
 */
public class StackAllocTest {

    public static void main(String[] args) throws InterruptedException {
        int size = 500000;
        for (int i = 0; i < size; i++) {
            // 关闭逃逸分析：-XX:-DoEscapeAnalysis - 导致所有对象都会在堆上分配，可能引起不必要的gc
            // 开启逃逸分析：-XX:+DoEscapeAnalysis - 可以让部分对象直接分配在栈上，随着方法的结束而直接消除
            alloc();
        }

        MINUTES.sleep(5);
    }

    private static Student alloc() {
        return new Student(11L, 11);
    }

    static class Student {
        long id;

        int age;

        public Student(long id, int age) {
            this.id = id;
            this.age = age;
        }
    }
}
