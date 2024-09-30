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
     * 删除资源告警信息
     *
     * @param idsDTO 资源告警信息id
     */
    void deleteResourceAlarmMessage(IdsDTO idsDTO);

    /**
     * 产生授权资源告警信息（平台授权以及租户授权）
     *
     * @param resourceEnum 资源枚举
     * @param tenantId 租户id
     * @param alarm 是否告警 0 - 告警  1 - 正常
     */
    void createAuthResourceAlarmMessage(ResourceEnum resourceEnum, Long tenantId, Byte alarm);

    /**
     * 产生告警信息
     *
     * @param resourceAlarmMessageList 告警信息列表
     */
    void generatedAlarmList(List<ResourceAlarmMessage> resourceAlarmMessageList);
}
