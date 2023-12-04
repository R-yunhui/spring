package com.ral.young.study.designpattern.structuraltype.adapterpattern;

/**
 * 微信登录接口适配器
 *
 * @author renyunhui
 * @date 2023-12-04 11:17
 * @since 1.0.0
 */
public class WeChatLoginServiceAdapter {

    private final LoginService loginService;

    public WeChatLoginServiceAdapter() {
        this.loginService = new LoginService();
    }

    public void loginWithWechat(String weChatId, String password) {
        // 微信登录
        System.out.println("微信登录");
        loginService.login(weChatId, password);
    }
}
