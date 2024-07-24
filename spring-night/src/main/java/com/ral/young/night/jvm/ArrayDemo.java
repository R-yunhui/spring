package com.ral.young.night.jvm;

/**
 * 验证数组的类加载器
 *
 * @author renyunhui
 * @date 2024-07-24 9:58
 * @since 1.0.0
 */
public class ArrayDemo {

    public static void main(String[] args) {
        // 数组类的类对象不是由类加载器创建的，而是根据 Java 运行时的要求自动创建的。
        // Class.getClassLoader()返回的数组类的类加载器与其元素类型的类加载器相同；如果元素类型是原始类型，则数组类没有类加载器
        int[] arr = new int[3];
        ClassLoader classLoader = arr.getClass().getClassLoader();
        if (null == classLoader) {
            System.out.println("如果元素类型是原始类型，则数组类没有类加载器。");
        } else {
            System.out.println(classLoader.getClass().getName());
        }

        User[] users = new User[3];
        System.out.println(users.getClass().getClassLoader().getClass().getName());
    }

    static class User {
        int id;
    }
}
