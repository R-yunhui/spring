package com.ral.young.spring.basic.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * ID生成器工具类
 */
@Slf4j
@Component
public class IdGenerator {

    /**
     * 终端ID
     */
    private long workerId;
    
    /**
     * 数据中心ID
     */
    private long datacenterId;
    
    /**
     * 雪花算法对象
     */
    private Snowflake snowflake;

    /**
     * 初始化雪花算法
     */
    @PostConstruct
    public void init() {
        try {
            // 获取本机的IP地址编码
            workerId = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());
            // 取余，确保在0-31之间
            workerId = workerId % 32;
            // 数据中心ID默认为1
            datacenterId = 1;
            log.info("当前机器的workerId: {}", workerId);
            
            snowflake = IdUtil.getSnowflake(workerId, datacenterId);
        } catch (Exception e) {
            log.error("初始化雪花算法异常", e);
            // 如果获取失败，则使用备用的配置
            workerId = 1;
            datacenterId = 1;
            snowflake = IdUtil.getSnowflake(workerId, datacenterId);
        }
    }

    /**
     * 获取雪花算法生成的ID
     */
    public long nextId() {
        return snowflake.nextId();
    }

    /**
     * 获取雪花算法生成的ID字符串
     */
    public String nextIdStr() {
        return snowflake.nextIdStr();
    }

    /**
     * 获取当前的workerId
     */
    public long getWorkerId() {
        return workerId;
    }

    /**
     * 获取当前的datacenterId
     */
    public long getDatacenterId() {
        return datacenterId;
    }
    
    /**
     * 根据前缀创建带格式的ID
     * 
     * @param prefix 前缀
     * @return 格式化的ID
     */
    public String nextFormattedId(String prefix) {
        return String.format("%s%s", prefix, snowflake.nextIdStr());
    }
    
    /**
     * 自定义workerId和datacenterId创建ID
     * 
     * @param workerId 工作机器ID
     * @param datacenterId 数据中心ID
     * @return ID
     */
    public long nextId(long workerId, long datacenterId) {
        return IdUtil.getSnowflake(workerId, datacenterId).nextId();
    }
} 