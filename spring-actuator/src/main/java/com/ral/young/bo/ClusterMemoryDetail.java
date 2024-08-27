package com.ral.young.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author renyunhui
 * @description 这是一个ClusteCpuDetail类
 * @date 2024-08-22 10-22-25
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClusterMemoryDetail {

    private String nodeName;

    private String instance;

    /**
     * 集群内存使用详情 key：时间戳  value：内存使用率
     */
    private Map<Long, String> capMemroyDetailMap;
}
