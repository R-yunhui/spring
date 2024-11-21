package com.ral.young.spring.basic.service.impl;

import com.ral.young.spring.basic.entity.User;
import com.ral.young.spring.basic.mapper.UserMapper;
import com.ral.young.spring.basic.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
} 