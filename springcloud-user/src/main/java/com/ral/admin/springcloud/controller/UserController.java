package com.ral.admin.springcloud.controller;

import cn.hutool.core.util.IdUtil;
import com.ral.admin.springcloud.common.BaseResult;
import com.ral.admin.springcloud.pojo.UserDo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-08 13:39
 * @Describe:
 * @Modify:
 */
@RestController
public class UserController {

    @GetMapping(value = "/getUserById/{id}")
    public BaseResult<UserDo> getUserById(@PathVariable(value = "id") int id) {
        return BaseResult.success(UserDo.builder()
                .id(id)
                .userId(IdUtil.fastSimpleUUID())
                .username("mike")
                .realName("麦克")
                .build());
    }

    @GetMapping(value = "/user/{id}")
    public BaseResult<String> user(@PathVariable(value = "id") int id) {
        return BaseResult.success("访问springcloud-user服务成功：" + id);
    }
}
