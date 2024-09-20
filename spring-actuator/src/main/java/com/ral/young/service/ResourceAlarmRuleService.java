package com.ral.young.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ral.young.bo.ResourceAlarmRule;
import com.ral.young.vo.ResourceAlarmRuleVO;

import java.util.List;

/**
 * @author renyunhui
 * @description 这是一个ResourceAlarmRuleService类
 * @date 2024-09-19 17-22-31
 * @since 1.2.0
 */
public interface ResourceAlarmRuleService extends IService<ResourceAlarmRule> {

    /**
     * 保存或修改资源告警规则配置
     *
     * @param resourceAlarmRuleVOS 资源告警规则配置
     */
    void saveOrUpdateResourceAlarmRule(List<ResourceAlarmRuleVO> resourceAlarmRuleVOS);

    /**
     * 查询资源告警规则配置
     *
     * @return 资源告警规则配置
     */
    List<ResourceAlarmRuleVO> queryResourceAlarmRule();

    /**
     * 查询全部资源告警规则配置
     *
     * @return 全部资源告警规则配置
     */
    List<ResourceAlarmRule> queryAllResourceAlarmRule();
}
