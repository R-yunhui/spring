package com.ral.young.practice.sprint.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Resource;

/**
 *
 * @author renyunhui
 * @date 2023-05-24 10:45
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Car {

    private Long id;

    private String name;

    @Resource
    private User user;

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public Car(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
