package com.ral.young.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ral.young.bo.ResourceAlarmMessage;
import com.ral.young.enums.ResourceEnum;
import com.ral.young.vo.ResourceAlarmMessageVO;

import java.util.List;

/**
 * @author renyunhui
 * @description 这是一个ResourceAlarmMessageService类
 * @date 2024-09-19 17-23-38
 * @since 1.2.0
 */
public interface ResourceAlarmMessageService extends IService<ResourceAlarmMessage> {

    /**
     * 查询资源告警信息
     *
     * @return List<ResourceAlarmMessageVO> 资源告警信息
     */
    List<ResourceAlarmMessageVO> queryResourceAlarmMessage();

    /**
     * 根据租户id和开始结束时间查询告警信息
     *
     * @param tenantId 租户id
     * @param start    开始时间
     * @param end      结束时间
     * @return 告警信息
     */
    List<ResourceAlarmMessage> queryResourceAlarmMessageByTenantId(Long tenantId, Long start, Long end);

    /**
     * 删除资源告警信息
     *
     * @param ids 资源告警信息id
     * @return 是否删除成功
     */
    Boolean deleteResourceAlarmMessage(List<Long> ids);

    /**
     * 产生资源告警信息
     *
     * @param resourceEnum 资源枚举
     * @param tenantId     租户id
     * @param ruleId       规则id
     * @param threshold    阈值
     */
    void createResourceAlarmMessage(ResourceEnum resourceEnum, Long tenantId, Long ruleId, Double threshold);

    /**
     * 产生资源告警信息
     *
     * @param resourceEnum 资源枚举
     * @param tenantId     租户id
     * @param ruleId       规则id
     */
    void createResourceAlarmMessage(ResourceEnum resourceEnum, Long tenantId, Long ruleId);
}
