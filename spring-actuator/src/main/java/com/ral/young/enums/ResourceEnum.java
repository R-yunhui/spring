package com.ral.young.enums;

import lombok.Getter;

/**
 * @author renyunhui
 * @description 这是一个ResourceEnum类
 * @date 2024-08-28 14-55-15
 * @since 1.1.2
 */
@Getter
public enum ResourceEnum {

    /**
     * CPU
     */
    CPU("cpu", "CPU负载超过%s%%，请及时处理！"),

    /**
     * 内存
     */
    MEMORY("内存", "CPU负载超过%s%%，请及时处理！"),

    /**
     * 磁盘
     */
    DISK("磁盘", "磁盘负载超过%s%%，请及时处理！"),

    /**
     * GPU
     */
    GPU("GPU", "CPU负载超过%s%%，请及时处理！"),

    /**
     * 磁盘IO
     */
    DISK_IO("磁盘IO", ""),

    /**
     * 网络IO
     */
    NETWORK_IO("网络IO", ""),

    /**
     * 平台授权
     */
    PLATFORM_AUTH("平台授权", "授权已过期，请及时联系运维人员重新授权！"),

    /**
     * 租户授权
     */
    TENANT_AUTH("租户授权", "授权已过期，请及时联系运维人员重新授权！");

    private final String desc;

    private final String alarmMessage;

    ResourceEnum(String desc, String alarmMessage) {
        this.desc = desc;
        this.alarmMessage = alarmMessage;
    }
}
