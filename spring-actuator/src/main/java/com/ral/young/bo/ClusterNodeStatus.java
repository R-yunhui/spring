package com.ral.young.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author renyunhui
 * @description 这是一个ClusterNodeStatus类
 * @date 2024-08-21 14-59-26
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClusterNodeStatus {

    private Integer readyNode;

    private Integer failNode;

    private Integer allNode;
}
