package com.ral.young.study.designpattern.behavioral.policypattern;

/**
 * 角色校验处理器
 *
 * @author renyunhui
 * @date 2023-12-04 10:13
 * @since 1.0.0
 */
public class RoleCheckHandler extends LoginCheckHandler {

    @Override
    public void doHandler(LoginUser loginUser) {
        if (1 != loginUser.getRole()) {
            System.err.println("角色信息不正确");
            return;
        }


        System.out.println("角色信息校验通过！");
        next.doHandler(loginUser);
    }
}
