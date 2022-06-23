package com.ral.young.ioc;

/**
 * @author renyunhui
 * @date 2022-06-20 14:21
 * @since 1.0.0
 */
public class Car {

    private Integer id;

    private String name;

    public Car() {

    }

    public Car(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Car{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
