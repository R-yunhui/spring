package com.ral.young.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author renyunhui
 * @description 这是一个MetricsQuery类
 * @date 2024-08-26 09-49-25
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MetricsQuery {

    /**
     * 指标名称
     */
    private String metricsTag;

    private String dateTime;

    private String nodeName;

    private String instance;
}
