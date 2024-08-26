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
    public static final String SUM_KUBE_NODE_STATUS_ALLOCATABLE_MEMORY = "sum(kube_node_status_allocatable{origin_prometheus=~\"\",resource=\"memory\", unit=\"byte\", node=~\"$nodeName$\"})";

    /**
     * 集群已使用内存
     */
    public static final String SUM_CONTAINER_MEMORY_WORKING_SET_BYTES = "sum(container_memory_working_set_bytes{origin_prometheus=~\"\",container!=\"\",node=~\"$nodeName$\"})";

    /**
     * 磁盘总量
     */
    public static final String SUM_CONTAINER_FS_LIMIT_BYTES = "sum(container_fs_limit_bytes{origin_prometheus=~\"\",device=~\"^/dev/.*$\",id=\"/\",node=~\"$nodeName$\"})";

    /**
     * 磁盘使用量
     */
    public static final String SUM_CONTAINER_FS_USAGE_BYTES = "sum(container_fs_usage_bytes{origin_prometheus=~\"\",device=~\"^/dev/.*$\",id=\"/\",node=~\"$nodeName$\"})";

    /**
     * cpu总核数
     */
    public static final String SUM_KUBE_NODE_STATUS_ALLOCATABLE_CPU = "sum(kube_node_status_allocatable{origin_prometheus=~\"\",resource=\"cpu\", unit=\"core\", node=~\"$nodeName$\"})";

    /**
     * cpu核心数使用量
     */
    public static final String SUM_IRATE_CONTAINER_CPU_USAGE_SECONDS_TOTAL = "sum(irate(container_cpu_usage_seconds_total{origin_prometheus=~\"\",id=\"/\",node=~\"$nodeName$\"}[2m]))";

    /**
     * 集群节点内存使用率
     */
    public static final String SUM_CONTAINER_MEMORY_WORKING_SET_BYTES_DETAIL = "sum(container_memory_working_set_bytes{origin_prometheus=~\"\",container!=\"\",node=~\"$nodeName$\"})by (node) / sum(kube_node_status_allocatable{origin_prometheus=~\"\",resource=\"memory\", unit=\"byte\", node=~\"$nodeName$\"})by (node)*100";

    /**
     * 磁盘使用率
     */
    public static final String SUM_CONTAINER_FS_USAGE_BYTES_DETAIL = "sum(container_fs_usage_bytes{origin_prometheus=~\"\",device=~\"^/dev/.*$\",id=\"/\",node=~\"$nodeName$\"})by (node) / sum (container_fs_limit_bytes{origin_prometheus=~\"\",device=~\"^/dev/.*$\",id=\"/\",node=~\"$nodeName$\"})by (node)";

    /**
     * 集群节点 CPU 使用率
     */
    public static final String SUM_IRATE_CONTAINER_CPU_USAGE_SECONDS_TOTAL_DETAIL = "sum(irate(container_cpu_usage_seconds_total{origin_prometheus=~\"\",container!=\"\",node=~\"$nodeName$\"}[2m]))by (node) / sum(kube_node_status_allocatable{origin_prometheus=~\"\",resource=\"cpu\", unit=\"core\", node=~\"$nodeName$\"})by (node)*100";

    /**
     * 网络接收
     */
    public static final String SUM_IRATE_CONTAINER_NETWORK_RECEIVE_BYTES_TOTAL_RECEIVE = "sum(irate(container_network_receive_bytes_total{origin_prometheus=~\"\",node=~\"$nodeName$\",namespace=~\".*\"}[2m]))*8";

    /**
     * 网络发送
     */
    public static final String SUM_IRATE_CONTAINER_NETWORK_TRANSMIT_BYTES_TOTAL_SEND = "sum(irate(container_network_transmit_bytes_total{origin_prometheus=~\"\",node=~\"$nodeName$\",namespace=~\".*\"}[2m]))*8";

    /**
     * 磁盘接收
     */
    public static final String SUM_IRATE_CONTAINER_FS_READS_BYTES_TOTAL = "sum(irate(container_fs_reads_bytes_total{origin_prometheus=~\"\",node=~\"$nodeName$\",namespace=~\".*\"}[2m]))*8";


    /**
     * 磁盘发送
     */
    public static final String SUM_IRATE_CONTAINER_FS_WRITE_BYTES_TOTAL = "sum(irate(container_fs_write_bytes_total{origin_prometheus=~\"\",node=~\"$nodeName$\",namespace=~\".*\"}[2m]))*8";

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
    public static final String DCGM_GPU_USED_UTIL_RATIO = "sum(DCGM_FI_DEV_FB_USED{Hostname=\"$nodeName$\"}) by (Hostname) / (sum(DCGM_FI_DEV_FB_USED{Hostname=\"$nodeName$\"}) by (Hostname) + sum(DCGM_FI_DEV_FB_FREE{Hostname=\"$nodeName$\"}) by (Hostname))";

    /**
     * gpu 内存使用量
     */
    public static final String DCGM_GPU_USED_MEMORY = "sum(DCGM_FI_DEV_FB_USED{Hostname=\"$nodeName$\"}) by (Hostname)";

    /**
     * gpu 空闲内存
     */
    public static final String DCGM_GPU_FREE_MEMORY = "sum(DCGM_FI_DEV_FB_FREE{Hostname=\"$nodeName$\"}) by (Hostname)";
}
