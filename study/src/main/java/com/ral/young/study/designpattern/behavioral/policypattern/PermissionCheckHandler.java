package com.ral.young.study.designpattern.behavioral.policypattern;

/**
 * 权限校验处理器
 *
 * @author renyunhui
 * @date 2023-12-04 10:11
 * @since 1.0.0
 */
public class PermissionCheckHandler extends LoginCheckHandler {

    @Override
    public void doHandler(LoginUser loginUser) {
        if (1 != loginUser.getPermission()) {
            System.err.println("用户权限不正确");
            return;
        }

        System.out.println("权限信息校验通过！");
        // 责任链末尾，不需要再继续执行
    }
}
