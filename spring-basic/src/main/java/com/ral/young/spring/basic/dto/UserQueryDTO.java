package com.ral.young.spring.basic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ApiModel(description = "用户查询条件")
public class UserQueryDTO {

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "性别：0-未知，1-男，2-女")
    private Integer gender;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "创建时间起始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTimeStart;

    @ApiModelProperty(value = "创建时间结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTimeEnd;

    @ApiModelProperty(value = "当前页码", required = true, example = "1")
    private Integer current = 1;

    @ApiModelProperty(value = "每页数量", required = true, example = "10")
    private Integer size = 10;
} 