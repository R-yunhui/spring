package com.ral.young.transaction.service.impl;

import cn.hutool.json.JSONUtil;
import com.ral.young.transaction.User;
import com.ral.young.transaction.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author renyunhui
 * @date 2022-07-06 17:21
 * @since 1.0.0
 */
@Service(value = "userService")
@Slf4j
public class UserServiceImpl implements IUserService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addUser(User user) {
        int count = jdbcTemplate.update("insert into user (id, name) values (?, ?)", user.getId(), user.getName());
        log.info("增加用户数量:{}", count);
        return count;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateUser(User user) {
        int count = jdbcTemplate.update("update user set id = ?, name = ? where id =" + user.getId(), user.getId(), user.getName());
        log.info("修改用户数量:{}", count);
        return count;
    }

    @Override
    public User selectUser(int id) {
        User user = jdbcTemplate.queryForObject("select * from user where id = " + id, (resultSet, i) -> new User(resultSet.getInt("id"), resultSet.getNString("name")));
        log.info("查询到的结果:{}", JSONUtil.toJsonStr(user));
        return user;
    }
}
