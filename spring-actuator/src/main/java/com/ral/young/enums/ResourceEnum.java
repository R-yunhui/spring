package com.ral.young.enums;

/**
 * @author renyunhui
 * @description 这是一个ResourceEnum类
 * @date 2024-08-28 14-55-15
 * @since 1.1.2
 */
public enum ResourceEnum {

    /**
     * CPU
     */
    CPU("cpu"),

    /**
     * 内存
     */
    MEMORY("内存"),

    /**
     * 磁盘
     */
    DISK("磁盘"),

    /**
     * GPU
     */
    GPU("GPU"),

    /**
     * 磁盘IO
     */
    DISK_IO("磁盘IO"),

    /**
     * 网络IO
     */
    NETWORK_IO("网络IO");

    private String desc;

    ResourceEnum(String desc) {
        this.desc = desc;
    }
}
