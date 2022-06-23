package com.ral.young.ioc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

/**
 * {@link InstantiationAwareBeanPostProcessor}
 *
 * @author renyunhui
 * @date 2022-06-20 19:57
 * @since 1.0.0
 */
@Slf4j
// @Component
public class MyInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (beanClass.equals(Car.class)) {
            log.info("实例化Bean之前执行的 BeanPostProcessor => Car.Class");
            return new Car(4, "奥迪");
        }
        return null;
    }
}
