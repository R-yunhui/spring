package com.ral.young.night.project.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ral.young.night.project.enums.SexEnum;
import com.ral.young.night.project.mapper.UserMapper;
import com.ral.young.night.project.po.User;
import com.ral.young.night.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 *
 * @author renyunhui
 * @date 2024-06-26 14:48
 * @since 1.0.0
 */
@Service(value = "userService")
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int initUserInfo(int size) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("构造用户数据");
        List<User> users = generateUserInfo(size);
        stopWatch.stop();

        stopWatch.start("构造完成的用户数据入库");
        Integer column = userMapper.insertBatchSomeColumn(users);
        stopWatch.stop();

        stopWatch.start("将用户数据缓存到redis");
        users.forEach(user -> {
            String key = "userId::" + user.getId();
            redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(user));
        });
        stopWatch.stop();

        log.info("初始化用户数据完成，耗时：{}", stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        return column;
    }

    private List<User> generateUserInfo(int size) {
        List<User> users = new ArrayList<>();
        IntStream.range(0, size).forEach(o -> {
            User user = new User();
            user.setId(IdUtil.getSnowflakeNextId());
            user.setName(IdUtil.fastSimpleUUID());
            user.setAge(ThreadLocalRandom.current().nextInt(10) + 20);
            user.setSex(SexEnum.MAN);
            user.setUpdateUser(IdUtil.getSnowflakeNextId());
            user.setCreateUser(IdUtil.getSnowflakeNextId());
            user.setCreateTime(DateUtil.date());
            user.setUpdateTime(DateUtil.date());
            users.add(user);
        });
        return users;
    }
}
