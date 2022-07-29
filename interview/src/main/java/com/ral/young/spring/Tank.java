package com.ral.young.spring;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 *
 * @author renyunhui
 * @date 2022-07-29 16:06
 * @since 1.0.0
 */
@Order(1)
@Component
public class Tank implements ITank {

    private int id;

    private String name;

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

    public Tank() {
        System.out.println("tank init");
        this.id = 1;
        this.name = "haha";
    }
}
