package com.ral.young.spring;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 *
 * @author renyunhui
 * @date 2022-07-29 16:06
 * @since 1.0.0
 */
@Component
public class Car {

    private Tank tank;

    private int id;

    public Tank getTank() {
        return tank;
    }

    public void setTank(Tank tank) {
        this.tank = tank;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Car(ObjectProvider<Tank> provider) {
        this.tank = provider.getIfAvailable();

        // 通过 Bean 的优先级去获取
         // provider.orderedStream().findFirst().orElse(null);
    }
}
