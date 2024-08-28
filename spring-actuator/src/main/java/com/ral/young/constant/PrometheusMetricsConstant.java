package com.ral.young.constant;

/**
 * @author renyunhui
 * @description Prometheus 的埋点标签定义
 * @date 2024-08-22 13-36-20
 * @since 1.2.0
 */
public class PrometheusMetricsConstant {
    
    public static final String ALL_NODE_NAME = "^.*$";

    public static final String NODE_NAME_REPLACE_TAG = "$nodeName$";

    public static final String INSTANCE_NAME_REPLACE_TAG = "$instance$";

    /**
     * 所有节点数
     */
    public static final String SUM_KUBE_NODE_INFO = "sum(kube_node_info)";

    /**
     * 不可用节点数
     */
    public static final String SUM_KUBE_NODE_SPEC_UNSCHEDULABLE = "sum(kube_node_spec_unschedulable)";

    /**
     * 集群总内存
     */
    public static final String SUM_NODE_TOTAL_MEMORY = "sum(node_memory_MemTotal_bytes{origin_prometheus=~\"\",job=~\"kubernetes-service-endpoints\",instance=~\"$instance$\"}) by (instance)";

    /**
     * 集群已使用内存
     */
    public static final String SUM_NODE_FREE_MEMORY = "sum(node_memory_MemTotal_bytes{origin_prometheus=~\"\",job=~\"kubernetes-service-endpoints\",instance=~\"$instance$\"}) by (instance) - sum(node_memory_MemFree_bytes{origin_prometheus=~\"\",job=~\"kubernetes-service-endpoints\",instance=~\"$instance$\"}) by (instance)";

    /**
     * 集群节点内存使用率
     */
    public static final String SUM_NODE_MEMORY_USED_RATE = "(sum(node_memory_MemTotal_bytes{origin_prometheus=~\"\",job=~\"kubernetes-service-endpoints\",instance=~\"$instance$\"} - node_memory_MemAvailable_bytes{origin_prometheus=~\"\",job=~\"kubernetes-service-endpoints\",instance=~\"$instance$\"}) by (instance) / sum(node_memory_MemTotal_bytes{origin_prometheus=~\"\",job=~\"kubernetes-service-endpoints\",instance=~\"$instance$\"}) by (instance))*100";

    /**
     * 磁盘总量
     */
    public static final String SUM_CONTAINER_FS_LIMIT_BYTES = "sum(node_filesystem_size_bytes{origin_prometheus=~\"\",job=~\"kubernetes-service-endpoints\",instance=~\"$instance$\"})by(instance)";

    /**
     * 磁盘使用量
     */
    public static final String SUM_CONTAINER_FS_USAGE_BYTES = "sum(node_filesystem_size_bytes{origin_prometheus=~\"\",job=~\"kubernetes-service-endpoints\",instance=~\"$instance$\"})by(instance) - sum(node_filesystem_free_bytes{origin_prometheus=~\"\",job=~\"kubernetes-service-endpoints\",instance=~\"$instance$\"})by(instance)";

    /**
     * 磁盘使用率
     */
    public static final String SUM_CONTAINER_FS_USAGE_BYTES_DETAIL = "(sum(node_filesystem_size_bytes{origin_prometheus=~\"\",job=~\"kubernetes-service-endpoints\",instance=~\"$instance$\"})by(instance) - sum(node_filesystem_free_bytes{origin_prometheus=~\"\",job=~\"kubernetes-service-endpoints\",instance=~\"$instance$\"})by(instance)) / sum(node_filesystem_size_bytes{origin_prometheus=~\"\",job=~\"kubernetes-service-endpoints\",instance=~\"$instance$\"})by(instance)";

    /**
     * cpu核数
     * count(node_cpu_seconds_total{origin_prometheus=~\"\",job=~\"kubernetes-service-endpoints\",mode='system',instance=~\"$instance$\"}) by (instance)
     */
    public static final String SUM_KUBE_NODE_STATUS_ALLOCATABLE_CPU = "count(node_cpu_seconds_total{origin_prometheus=~\"\",job=~\"kubernetes-service-endpoints\",mode='system',instance=~\"$instance$\"}) by (instance)";

    /**
     * cpu核心使用量
     */
    public static final String SUM_IRATE_CONTAINER_CPU_USAGE_SECONDS_TOTAL = "sum(node_cpu_seconds_total{mode!=\"idle\", instance=~\"$instance$\"}) by (instance)";

    /**
     * 集群节点 CPU 使用率
     */
    public static final String SUM_IRATE_CONTAINER_CPU_USAGE_SECONDS_TOTAL_DETAIL = "100 - (avg by(instance) (irate(node_cpu_seconds_total{mode=\"idle\", instance=~\"$instance$\"}[5m])) * 100)";

    /**
     * 网络接收
     */
    public static final String SUM_NETWORK_RECEIVE_BYTES_TOTAL = "sum(node_network_receive_bytes_total{origin_prometheus=~\"\",job=~\"kubernetes-service-endpoints\",instance=~\"$instance$\"}) by (instance)";

    /**
     * 网络发送
     */
    public static final String SUM_NETWORK_TRANSMIT_BYTES_TOTAL = "sum(node_network_receive_bytes_total{origin_prometheus=~\"\",job=~\"kubernetes-service-endpoints\",instance=~\"$instance$\"}) by (instance)";

    /**
     * 磁盘接收
     */
    public static final String SUM_NODE_DISK_READ_TOTAL_BYTES = "sum(node_network_transmit_bytes_total{origin_prometheus=~\"\",instance=~\"$instance$\"}) by (instance)";


    /**
     * 磁盘发送
     */
    public static final String SUM_NODE_DISK_WRITE_TOTAL_BYTES = "sum(node_disk_written_bytes_total{origin_prometheus=~\"\",instance=~\"$instance$\"}) by (instance)";

    /**
     * 节点资源信息
     */
    public static final String NODE_UNAME_INFO = "node_uname_info";

    /**
     * 通过 dcgm-exporter 采集的 gpu 利用率
     */
    public static final String DCGM_FI_DEV_GPU_UTIL = "DCGM_FI_DEV_GPU_UTIL{Hostname=~\"$nodeName$\"}";

    /**
     * gpu 使用率
     */
    public static final String DCGM_GPU_USED_UTIL_RATIO = "sum(DCGM_FI_DEV_FB_USED{Hostname=~\"$nodeName$\"}) by (Hostname) / sum(DCGM_FI_DEV_FB_USED{Hostname=~\"$nodeName$\"} + DCGM_FI_DEV_FB_FREE{Hostname=~\"$nodeName$\"}) by (Hostname)";

    /**
     * gpu 内存使用量 显存
     */
    public static final String DCGM_GPU_USED_MEMORY = "sum(DCGM_FI_DEV_FB_USED{Hostname=~\"$nodeName$\"}) by (Hostname,gpu)";

    /**
     * gpu 空闲内存 显存
     */
    public static final String DCGM_GPU_FREE_MEMORY = "sum(DCGM_FI_DEV_FB_FREE{Hostname=~\"$nodeName$\"}) by (Hostname,gpu)";
}
