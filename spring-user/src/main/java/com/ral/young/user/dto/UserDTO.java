package com.ral.young.user.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ral.young.enums.BaseEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户实体
 *
 * @author renyunhui
 * @date 2023-06-20 14:33
 * @since 1.0.0
 */
@TableName(value = "ral_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "username")
    private String username;

    @TableField(value = "password")
    private String password;

    @TableField(value = "age")
    private Integer age;

    @TableField(value = "sex")
    private BaseEnums.GenderEnum sex;

    @TableLogic(value = "is_delete")
    private Integer isDelete;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(value = "create_user")
    private Long createUser;

    @TableField(value = "update_user")
    private Long updateUser;
}
