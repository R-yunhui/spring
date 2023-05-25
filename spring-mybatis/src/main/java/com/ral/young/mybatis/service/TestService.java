package com.ral.young.mybatis.service;

import com.ral.young.mybatis.dao.UserMapper;
import com.ral.young.mybatis.dto.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author renyunhui
 * @date 2023-05-24 13:47
 * @since 1.0.0
 */
@Service
@Slf4j
public class TestService {

    @Resource
    private UserMapper userMapper;

    @Transactional
    public void update() {
        User user = new User(1L, "bob", 11);
        int i = userMapper.updateById(user);
        log.info("修改的行数:{}", i);
    }

    @Transactional
    public void add() {
        User user = new User(1L, "bob", 12);
        int insert = userMapper.insert(user);
        log.info("增加的行数:{}", insert);
    }

    public void query() {
        List<User> users = userMapper.selectList(null);
        log.info("查询到的数据量:{}", users.size());
    }

}
