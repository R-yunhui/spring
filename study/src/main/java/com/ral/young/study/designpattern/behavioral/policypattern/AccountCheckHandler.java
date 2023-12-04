package com.ral.young.study.designpattern.behavioral.policypattern;

import cn.hutool.core.util.StrUtil;

/**
 * 用户校验处理器
 *
 * @author renyunhui
 * @date 2023-12-04 10:08
 * @since 1.0.0
 */
public class AccountCheckHandler extends LoginCheckHandler {

    @Override
    public void doHandler(LoginUser loginUser) {
        if (StrUtil.isBlank(loginUser.getUsername())) {
            System.err.println("用户名不能为空");
            return;
        }

        if (StrUtil.isBlank(loginUser.getPassword())) {
            System.err.println("密码不能为空");
            return;
        }

        if (!StrUtil.equals(loginUser.getPassword(), "123456")) {
            System.err.println("密码不正确");
            return;
        }

        System.out.println("基本信息校验通过！");
        next.doHandler(loginUser);
    }
}
