package ioc.test;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * @author renyunhui
 * @date 2022-06-20 14:21
 * @since 1.0.0
 */
@Component
public class Car implements FactoryBean<Car> {

    private Integer id;

    private String name;

    public Car() {
        this.id = 3;
        this.name = "奔驰";
    }

    public Car(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Car{" + "id=" + id + ", name='" + name + '\'' + '}';
    }

    @Override
    public Car getObject() throws Exception {
        return new Car(2, "宝马");
    }

    @Override
    public Class<?> getObjectType() {
        return Car.class;
    }

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }
}
