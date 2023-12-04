package com.ral.young.study.designpattern.structuraltype.adapterpattern;

/**
 * 登录接口 - 适配器模式 - 原接口
 *
 * @author renyunhui
 * @date 2023-12-04 11:16
 * @since 1.0.0
 */
public class LoginService {

    public void login(String username, String password) {
        System.out.println("登录成功,hello：" + username);
    }
}
