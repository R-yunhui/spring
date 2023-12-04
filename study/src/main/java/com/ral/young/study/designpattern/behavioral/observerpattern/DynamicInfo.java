package com.ral.young.study.designpattern.behavioral.observerpattern;

import lombok.Data;

import java.util.Date;

/**
 * 动态消息
 *
 * @author renyunhui
 * @date 2023-12-04 15:37
 * @since 1.0.0
 */
@Data
public class DynamicInfo {

    private String username;

    private String dynamicData;

    private Date publichDate;
}
