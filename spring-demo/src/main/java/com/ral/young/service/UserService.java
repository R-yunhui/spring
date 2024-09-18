package com.ral.young.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ral.young.dto.UserDTO;

/**
 * @author renyunhui
 * @description 这是一个UserService类
 * @date 2024-09-11 10-11-45
 * @since 1.0.0
 */
public interface UserService extends IService<UserDTO> {

    void test();
}
