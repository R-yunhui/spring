package com.ral.young.spring.basic.dubbo.provider.impl;

import com.ral.young.spring.basic.dubbo.provider.api.UserServiceApi;
import com.ral.young.spring.basic.service.UserService;
import com.ral.young.spring.basic.vo.UserVO;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author renyunhui
 * @description 这是一个UserServiceApiImpl类
 * @date 2024-11-28 10-22-57
 * @since 1.0.0
 */
@DubboService
public class UserServiceApiImpl implements UserServiceApi {

    @Resource
    private UserService userService;

    @Override
    public UserVO getUser(Long userId) {
        return userService.getUserById(userId);
    }
}
