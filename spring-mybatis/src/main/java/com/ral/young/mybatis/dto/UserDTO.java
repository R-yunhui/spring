package com.ral.young.mybatis.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ral.young.mybatis.enums.SexEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author renyunhui
 * @date 2023-05-24 13:43
 * @since 1.0.0
 */
@TableName(value = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "name")
    private String name;

    @TableField(value = "age")
    private Integer age;

    @TableField(value = "sex")
    private SexEnum sex;

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
