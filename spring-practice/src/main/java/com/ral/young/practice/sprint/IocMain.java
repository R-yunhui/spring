package com.ral.young.practice.sprint;

import cn.hutool.core.date.DateUtil;
import com.ral.young.practice.sprint.bean.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author renyunhui
 * @date 2023-05-24 10:34
 * @since 1.0.0
 */
public class IocMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(IocConfig.class);

        User bean = applicationContext.getBean(User.class);
        System.out.println("从 beanFactory 中获取到的 bean 对象：" + bean + " " + DateUtil.now());

        applicationContext.close();
    }
}
