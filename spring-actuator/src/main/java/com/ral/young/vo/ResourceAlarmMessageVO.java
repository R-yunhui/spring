package com.ral.young.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author renyunhui
 * @description 这是一个ResourceAlarmMessageVO类
 * @date 2024-09-19 17-47-22
 * @since 1.2.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceAlarmMessageVO {

    /**
     * 告警消息
     */
    private String message;

    /**
     * 告警时间
     */
    private Date alarmTime;
}
