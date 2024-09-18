package com.ral.young.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ral.young.dto.OrderDTO;
import com.ral.young.dto.UserDTO;
import com.ral.young.mapper.UserMapper;
import com.ral.young.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author renyunhui
 * @description 这是一个UserServiceImpl类
 * @date 2024-09-11 10-13-06
 * @since 1.0.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDTO> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public void test() {
        List<UserDTO> userDTOS = userMapper.selectJoinList(UserDTO.class, new MPJLambdaWrapper<>(UserDTO.class)
                .selectAll()
                .select(OrderDTO::getOrderName)
                .leftJoin(OrderDTO.class, OrderDTO::getBelongUserId, UserDTO::getId)
                .eq(UserDTO::getId, 1)
        );

//        SELECT
//        t.id,
//                t.ral_user_name,
//                t.ral_gender,
//                t.ral_age,
//                t.create_time,
//                t.update_time,
//                t1.ral_order_name
//        FROM
//        ral_user t
//        LEFT JOIN ral_order t1 ON ( t1.ral_belong_user_id = t.id )
//        WHERE
//                (
//                        t.id = ?)
    }
}
