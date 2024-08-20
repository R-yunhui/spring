package com.ral.young.bo;

import lombok.*;

import java.util.Map;

/**
 * @author renyunhui
 * @description 这是一个MetricsQueryBo类
 * @date 2024-08-19 16-54-13
 * @since 1.0.0
 */

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricsQuery extends MetricsQueryBase {

    /**
     * 查询的时间 IOS-8601 格式
     */
    private String dateTime;
}
