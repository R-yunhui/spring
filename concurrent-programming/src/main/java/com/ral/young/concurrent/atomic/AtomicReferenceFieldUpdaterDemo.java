package com.ral.young.concurrent.atomic;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * {@link java.util.concurrent.atomic.AtomicReferenceFieldUpdater}
 *
 * @author renyunhui
 * @date 2022-09-27 9:19
 * @since 1.0.0
 */
public class AtomicReferenceFieldUpdaterDemo {

    static final AtomicReferenceFieldUpdater<User, String> atomicReferenceFieldUpdater = AtomicReferenceFieldUpdater.newUpdater(User.class, String.class, "name");

    public static void main(String[] args) {
        User user = new User(1, "ryh");
        // 通过 cas 的方式修改对象的某个属性 （无锁优于加锁的方式）
        // 对应的属性上加上 volatile 修饰，保证一个线程的修改另一个线程可以及时的看见
        atomicReferenceFieldUpdater.getAndSet(user, "aaa");
        System.out.println(user.getName());
    }

    static class User {

        public int id;

        public volatile String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
