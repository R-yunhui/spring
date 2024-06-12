package com.ral.young.night.spring.ioc.service;

import com.ral.young.night.spring.ioc.bean.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * @author renyunhui
 * @date 2024-06-11 10:42
 * @since 1.0.0
 */
@Service
public class TestService {

    /*
     * @Resource
     *      1.默认通过属性名称注入，可以通过 @Resource(type = xxx.class) 指定类型
     *      2.只能用在 field setter 上面
     *      3.java 官方提供的自动注入注解
     * @Autowired
     *      1.默认通过类型注入，可以通过 @Qualifier 指定属性名称
     *      2.可以用在 field setter 构造器 上面
     *      3.spring 提供的自动注入注解
     */
    @Resource
    private User user;

    public void testOne() {
        System.out.println(user.getId());
    }
}
