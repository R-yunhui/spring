package com.ral.young.jvm;

/**
 *
 * @author renyunhui
 * @date 2022-07-27 14:17
 * @since 1.0.0
 */
public class User {

    private int id;

    private String name;

    public User() {
    }

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

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

    public void say() {
        System.out.println("===== 加载当前类的类加载器：" + this.getClass().getClassLoader() + " =====");
    }
}
