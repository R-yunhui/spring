package com.ral.young.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ral.young.bo.ResourceAlarmRule;
import com.ral.young.enums.ResourceEnum;
import com.ral.young.mapper.ResourceAlarmRuleMapper;
import com.ral.young.service.ResourceAlarmRuleService;
import com.ral.young.vo.ResourceAlarmRuleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author renyunhui
 * @description 这是一个ResourceAlarmRuleServiceImpl类
 * @date 2024-09-19 17-22-51
 * @since 1.2.0
 */
@Service
@Slf4j
public class ResourceAlarmRuleServiceImpl extends ServiceImpl<ResourceAlarmRuleMapper, ResourceAlarmRule> implements ResourceAlarmRuleService {

    @Resource
    private ResourceAlarmRuleMapper resourceAlarmRuleMapper;

    @Resource
    private ResourceAlarmRuleService resourceAlarmRuleService;

    @Override
    public void saveOrUpdateResourceAlarmRule(List<ResourceAlarmRuleVO> resourceAlarmRuleVOS) {
        // 每个租户针对不同资源的告警规则只能设置一种
        List<ResourceAlarmRule> dbResourceAlarmRule = resourceAlarmRuleMapper.selectList(new LambdaQueryWrapper<>(ResourceAlarmRule.class).eq(ResourceAlarmRule::getTenantId, 0L));
        if (CollUtil.isNotEmpty(dbResourceAlarmRule)) {
            Map<ResourceEnum, ResourceAlarmRuleVO> alarmRuleMap = resourceAlarmRuleVOS.stream().collect(Collectors.toMap(ResourceAlarmRuleVO::getResourceEnum, resourceAlarmRule -> resourceAlarmRule));
            for (ResourceAlarmRule resourceAlarmRule : dbResourceAlarmRule) {
                ResourceAlarmRuleVO resourceAlarmRuleVO = alarmRuleMap.get(resourceAlarmRule.getResourceEnum());
                if (resourceAlarmRuleVO != null) {
                    resourceAlarmRule.setThreshold(resourceAlarmRuleVO.getThreshold());
                    resourceAlarmRule.setTimeDuration(resourceAlarmRuleVO.getTimeDuration());
                    resourceAlarmRule.setUpdaterId(0L);
                }
            }
            resourceAlarmRuleService.updateBatchById(dbResourceAlarmRule);
        } else {
            List<ResourceAlarmRule> resourceAlarmRules = new ArrayList<>();
            for (ResourceAlarmRuleVO resourceAlarmRuleVO : resourceAlarmRuleVOS) {
                ResourceAlarmRule resourceAlarmRule = BeanUtil.copyProperties(resourceAlarmRuleVO, ResourceAlarmRule.class);
                resourceAlarmRule.setDeleteFlag((byte) 0);
                resourceAlarmRule.setTenantId(0L);
                resourceAlarmRule.setCreatorId(0L);
                resourceAlarmRule.setUpdaterId(0L);
            }
            resourceAlarmRuleService.saveBatch(resourceAlarmRules);
        }
    }

    @Override
    public List<ResourceAlarmRuleVO> queryResourceAlarmRule() {
        Long tenantId = 0L;
        // 每个租户针对不同资源的告警规则只能设置一种
        List<ResourceAlarmRule> dbResourceAlarmRule = resourceAlarmRuleMapper.selectList(new LambdaQueryWrapper<>(ResourceAlarmRule.class).eq(ResourceAlarmRule::getTenantId, tenantId));
        if (CollUtil.isEmpty(dbResourceAlarmRule)) {
            // 如果一开始该租户没有设置告警规则，则使用默认告警规则
            dbResourceAlarmRule = resourceAlarmRuleMapper.selectList(new LambdaQueryWrapper<>(ResourceAlarmRule.class).eq(ResourceAlarmRule::getTenantId, -1L));
        }
        return dbResourceAlarmRule.stream().map(resourceAlarmRule -> BeanUtil.copyProperties(resourceAlarmRule, ResourceAlarmRuleVO.class)).collect(Collectors.toList());
    }

    @Override
    public List<ResourceAlarmRule> queryAllResourceAlarmRule() {
        return resourceAlarmRuleMapper.selectList(null);
    }
}
