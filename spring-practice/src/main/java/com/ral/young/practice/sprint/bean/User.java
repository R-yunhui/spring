package com.ral.young.practice.sprint.bean;

import cn.hutool.core.date.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * {@link BeanClassLoaderAware}
 * {@link BeanNameAware}
 * {@link ApplicationContextAware}
 * 在 Bean 初始化之前执行，早于 {@link org.springframework.beans.factory.config.BeanPostProcessor} 的初始化前方法的回调
 *
 * @author renyunhui
 * @date 2023-05-24 10:24
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements BeanNameAware, BeanClassLoaderAware, ApplicationContextAware, InitializingBean, DisposableBean {

    private Long id;

    private String name;

    private int age;

    @Resource
    private Car car;

    public User(Long id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    @Override
    public void setBeanClassLoader(@NonNull ClassLoader classLoader) {
        System.out.println("五、BeanClassLoaderAware 调用：" + DateUtil.now());
    }

    @Override
    public void setBeanName(@NonNull String name) {
        System.out.println("四、BeanNameAware 调用：" + DateUtil.now());
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        System.out.println("六、ApplicationContextAware 调用：" + DateUtil.now());
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("十三、DisposableBean 调用：" + DateUtil.now());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("九、InitializingBean 调用：" + DateUtil.now());
    }

    public void customInit() {
        System.out.println("十、customInit 调用：" + DateUtil.now());
    }

    public void customDestroy() {
        System.out.println("十四、customDestroy 调用：" + DateUtil.now());
    }

    @PostConstruct
    public void postInit() {
        System.out.println("八、@PostConstruct 调用：" + DateUtil.now());
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("十二、@PreDestroy 调用：" + DateUtil.now());
    }
}
