package com.ral.young.study.designpattern.structuraltype.adapterpattern;

/**
 * 登录接口适配器
 *
 * @author renyunhui
 * @date 2023-12-04 11:17
 * @since 1.0.0
 */
public class QQChatLoginServiceAdapter {

    private final LoginService loginService;

    public QQChatLoginServiceAdapter() {
        this.loginService = new LoginService();
    }

    public void loginWithQq(String qqNum, String password) {
        // qq 登录
        System.out.println("qq登录");
        loginService.login(qqNum, password);
    }
}
