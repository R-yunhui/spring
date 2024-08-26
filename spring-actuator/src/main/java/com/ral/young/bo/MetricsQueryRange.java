package com.ral.young.bo;

import lombok.*;

/**
 * @author renyunhui
 * @description 这是一个MetricsQueryBo类
 * @date 2024-08-19 16-54-13
 * @since 1.0.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricsQueryRange {

    /**
     * 开始时间戳 unix_time
     */
    private String start;

    /**
     * 结束时间戳 unix_time
     */
    private String end;

    /**
     * 指标名称
     */
    private String metricsTag;

    /**
     * 以持续时间格式或浮点秒数为分辨率步长进行查询
     */
    private Float step;

    /**
     * 节点名称，默认为 ALL
     */
    private String nodeName;

    /**
     * 节点实力的 IP:PORT
     */
    private String instance;
}
