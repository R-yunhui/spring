package com.ral.young.user.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ral.young.user.dao.UserDao;
import com.ral.young.user.dto.UserDTO;
import com.ral.young.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 *
 * @author renyunhui
 * @date 2023-06-20 14:39
 * @since 1.0.0
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserDao, UserDTO> implements IUserService {

    @Override
    public String registerUser(UserDTO userDTO) {
        log.info("注册用户信息");

        Date now = DateUtil.date();
        userDTO.setCreateUser(userDTO.getId());
        userDTO.setUpdateUser(userDTO.getId());
        userDTO.setCreateTime(now);
        userDTO.setUpdateTime(now);
        save(userDTO);

        return JwtUtil.getJwtToken(userDTO.getId(), userDTO.getUsername());
    }

    @Override
    public UserDTO getUserById(Long userId) {
        log.info("getUserById:{}", userId);
        return getById(userId);
    }

}
