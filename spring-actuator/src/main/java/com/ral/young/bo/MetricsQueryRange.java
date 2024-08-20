package com.ral.young.bo;

import lombok.*;

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
public class MetricsQueryRange extends MetricsQueryBase {

    /**
     * 开始时间戳 IOS-8601 格式
     */
    private String start;

    /**
     * 结束时间戳 IOS-8601 格式
     */
    private String end;

    /**
     * 以持续时间格式或浮点秒数为分辨率步长进行查询
     */
    private Float step;
}
