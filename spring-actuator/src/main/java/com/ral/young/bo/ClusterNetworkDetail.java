package com.ral.young.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author renyunhui
 * @description ClusterNetworkDetail
 * @date 2024-08-21 15-44-20
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClusterNetworkDetail {

    private String nodeName;

    private Map<Long, Double> receiveBytes;

    private Map<Long, Double> sendBytes;
}
