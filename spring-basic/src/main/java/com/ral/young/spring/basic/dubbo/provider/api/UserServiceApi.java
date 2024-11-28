package com.ral.young.spring.basic.dubbo.provider.api;

import com.ral.young.spring.basic.vo.UserVO;

/**
 * @author renyunhui
 * @description 这是一个UserServiceApi类
 * @date 2024-11-28 10-22-29
 * @since 1.0.0
 */
public interface UserServiceApi {

    /**
     * 根据用户ID获取用户信息
     * @param userId 用户id
     * @return 用户信息
     */
    UserVO getUser(Long userId);
}
