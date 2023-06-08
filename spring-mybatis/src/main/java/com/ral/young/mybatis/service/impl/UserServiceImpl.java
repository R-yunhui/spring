package com.ral.young.mybatis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ral.young.mybatis.dao.UserMapper;
import com.ral.young.mybatis.dto.UserDTO;
import com.ral.young.mybatis.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * 用户持久层实现类
 *
 * @author renyunhui
 * @date 2023-06-08 14:24
 * @since 1.0.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDTO> implements IUserService {
}
