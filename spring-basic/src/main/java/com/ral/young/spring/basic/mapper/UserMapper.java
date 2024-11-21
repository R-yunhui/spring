package com.ral.young.spring.basic.mapper;

import com.ral.young.spring.basic.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends ExpandBaseMapper<User> {
} 