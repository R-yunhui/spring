package com.ral.young.practice.main;

import com.ral.young.practice.bean.Student;
import com.ral.young.practice.config.MyConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author renyunhui
 * @date 2022-11-30 18:49
 * @since 1.0.0
 */
public class Application {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MyConfig.class);

        Student bean = applicationContext.getBean(Student.class);
        System.out.println(bean);

        applicationContext.close();
    }
}
