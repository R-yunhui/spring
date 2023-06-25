package com.ral.young.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ral.young.user.dto.UserDTO;

/**
 * @author renyunhui
 * @date 2023-06-20 14:39
 * @since 1.0.0
 */
public interface IUserService extends IService<UserDTO> {

    /**
     * 注册用户信息
     *
     * @param userDTO 用户实体
     * @return 返回 token 信息
     */
    String registerUser(UserDTO userDTO);

    /**
     * 根据用户 id 获取用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    UserDTO getUserById(Long userId);
}
