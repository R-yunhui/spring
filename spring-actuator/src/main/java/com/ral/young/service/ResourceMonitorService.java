package com.ral.young.service;

import com.ral.young.bo.*;

import java.util.List;

/**
 * @author renyunhui
 * @description 资源监控接口定义
 * @date 2024-08-22 11-39-29
 * @since 1.2.0
 */
public interface ResourceMonitorService {

    /**
     * 查询某一时刻的节点资源信息
     *
     * @param nodeName     节点名称
     * @param instance     实例信息
     * @param resourceEnum 资源枚举
     * @return 资源信息
     */
    List<NodeResourceInfo> queryNodeResourceInfo(String nodeName, String instance, String resourceEnum);

    /**
     * 查询集群资源的变化情况
     *
     * @param metricsQueryRange 查询条件
     * @return 集群资源的变化情况
     */
    List<NodeResourceVariationInfo> queryNodeResourceVariationInfo(MetricsQueryRange metricsQueryRange);

    /**
     * 查看节点信息
     *
     * @return 集群的节点信息
     */
    List<ClusterNodeInfo> queryClusterNodeInfo();

    /**
     * 查询集群节点状态
     *
     * @return 集群节点状态
     */
    ClusterNodeStatus queryClusterNodeStatus();

    List<GpuInfo> queryGpuInfo();
}
