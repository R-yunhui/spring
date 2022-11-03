package com.ral.young.concurrent.atomic;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * {@link java.util.concurrent.atomic.AtomicIntegerFieldUpdater}
 *
 * @author renyunhui
 * @date 2022-09-27 9:26
 * @since 1.0.0
 */
public class AtomicIntegerFieldUpdaterDemo {

    static final AtomicIntegerFieldUpdater<User> atomic = AtomicIntegerFieldUpdater.newUpdater(User.class, "age");

    public static void main(String[] args) {
        User user = new User(18, "ryh");
        // 通过 cas 的方式修改对象的某个属性 （无锁优于加锁的方式）
        // 对应的属性上加上 volatile 修饰，保证一个线程的修改另一个线程可以及时的看见
        atomic.getAndIncrement(user);
        System.out.println(user.getAge());
    }

    static class User {
        public volatile int age;

        public String name;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public User(int age, String name) {
            this.age = age;
            this.name = name;
        }
    }
}
