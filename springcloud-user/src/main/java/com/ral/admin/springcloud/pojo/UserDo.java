package com.ral.admin.springcloud.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-08 13:42
 * @Describe:
 * @Modify:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDo {

    /** 主键ID */
    private int id;

    /** 用户ID */
    private String userId;

    /** 用户名 */
    private String username;

    /** 真实姓名 */
    private String realName;
}
