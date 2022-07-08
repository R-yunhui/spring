package com.ral.young.transaction;

import com.ral.young.transaction.service.IUserService;
import com.ral.young.transaction.service.impl.UserServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author renyunhui
 * @date 2022-07-06 17:36
 * @since 1.0.0
 */
public class Main {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(TransactionConfig.class);

        UserServiceImpl userService = (UserServiceImpl) applicationContext.getBean(IUserService.class);
        userService.selectUser(1);

        User user = new User(4, "bp");
        userService.addUser(user);

        user.setName("sam");
        userService.updateUser(user);
    }
}
