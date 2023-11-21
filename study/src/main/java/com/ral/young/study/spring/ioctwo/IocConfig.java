package com.ral.young.study.spring.ioctwo;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 *
 * @author renyunhui
 * @date 2023-11-21 11:04
 * @since 1.0.0
 */
@Component
public class IocConfig implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        Tank bean = applicationContext.getBean(Tank.class);
        TankTwo tankTwo = applicationContext.getBean(TankTwo.class);
        System.out.println(bean);
        System.out.println(tankTwo);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
