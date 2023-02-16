package com.ral.young.ioc;

/**
 *
 * @author renyunhui
 * @date 2023-02-16 10:51
 * @since 1.0.0
 */
public class Tank {

    private int id;

    private Car car;
    public Tank(int id) {
        this.id = id;
    }



    @Override
    public String toString() {
        return "Tank{" +
                "id=" + id +
                '}';
    }
}
