package com.ral.young.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author renyunhui
 * @description 这是一个ClusterNodeInfo类
 * @date 2024-08-22 17-40-08
 * @since 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClusterNodeInfo {

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点实例信息
     */
    private String nodeInstance;
}
