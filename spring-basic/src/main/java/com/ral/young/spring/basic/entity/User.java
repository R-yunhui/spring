package com.ral.young.spring.basic.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ral.young.spring.basic.enums.CommonEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private Integer age;

    private String email;

    private CommonEnum.GenderEnum gender;

    @TableLogic
    private Integer isDelete;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
} 