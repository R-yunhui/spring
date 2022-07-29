package com.ral.young.spring;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 *
 * @author renyunhui
 * @date 2022-07-29 16:06
 * @since 1.0.0
 */
@Order(2)
@Component
public class Tank2 implements ITank {

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

    public Tank2() {
        System.out.println("tank2 init");
        this.id = 2;
        this.name = "caca";
    }
}
