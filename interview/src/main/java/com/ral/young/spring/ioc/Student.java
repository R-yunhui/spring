package com.ral.young.spring.ioc;

import lombok.Data;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 *
 * @author renyunhui
 * @date 2023-02-16 16:02
 * @since 1.0.0
 */
@Data
public class Student {

    private int id;

    private String name;

    private User user;

    public Student(int id, String name, User user) {
        this.id = id;
        this.name = name;
        this.user = user;
    }

    public Student(ObjectProvider<User> user) {
        // 通过  ObjectProvider 防止出现 user 对象为 null 的情况，导致启动报错
        this.user = user.getIfAvailable();
    }
}
