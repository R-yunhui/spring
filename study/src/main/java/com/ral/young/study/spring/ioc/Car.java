package com.ral.young.study.spring.ioc;

import lombok.Data;

/**
 *
 * @author renyunhui
 * @date 2023-11-20 14:31
 * @since 1.0.0
 */
@Data
public class Car {

    private int id;

    private String name;

    public Car() {
    }

    public Car(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
