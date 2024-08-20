package com.ral.young.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author renyunhui
 * @description 这是一个MetricsQueryBo类
 * @date 2024-08-19 16-54-13
 * @since 1.0.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricsQueryBase {

    /**
     * 指标名称
     */
    private String metricsTag;

    /**
     * 指标的标签 k - labelName  v - labelValue
     */
    private Map<String, String> labelMap;
}
