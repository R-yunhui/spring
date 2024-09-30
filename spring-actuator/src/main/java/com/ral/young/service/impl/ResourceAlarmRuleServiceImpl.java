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
        UserDTO userInfo = UserUtils.getCurrentUserInfo();
        // 每个租户针对不同资源的告警规则只能设置一种
        List<ResourceAlarmRule> dbResourceAlarmRule = resourceAlarmRuleMapper.selectList(new LambdaQueryWrapper<>(ResourceAlarmRule.class)
                .eq(ResourceAlarmRule::getTenantId, userInfo.getTenantId())
                .eq(ResourceAlarmRule::getUserId, userInfo.getId())
                .eq(ResourceAlarmRule::getDeleteFlag, ResourceAlarmConstants.NOT_DELETE)
        );
        if (CollUtil.isNotEmpty(dbResourceAlarmRule)) {
            Map<ResourceEnum, ResourceAlarmRuleVO> alarmRuleMap = resourceAlarmRuleVOS.stream().collect(Collectors.toMap(ResourceAlarmRuleVO::getResourceEnum, resourceAlarmRule -> resourceAlarmRule));
            for (ResourceAlarmRule resourceAlarmRule : dbResourceAlarmRule) {
                ResourceAlarmRuleVO resourceAlarmRuleVO = alarmRuleMap.get(resourceAlarmRule.getResourceEnum());
                if (resourceAlarmRuleVO != null) {
                    resourceAlarmRule.setThreshold(resourceAlarmRuleVO.getThreshold());
                    resourceAlarmRule.setUserId(userInfo.getId());
                    resourceAlarmRule.setTimeDuration(resourceAlarmRuleVO.getTimeDuration());
                }
            }
            resourceAlarmRuleService.updateBatchById(dbResourceAlarmRule);
        } else {
            List<ResourceAlarmRule> resourceAlarmRules = new ArrayList<>();
            for (ResourceAlarmRuleVO resourceAlarmRuleVO : resourceAlarmRuleVOS) {
                ResourceAlarmRule resourceAlarmRule = BeanUtil.copyProperties(resourceAlarmRuleVO, ResourceAlarmRule.class);
                resourceAlarmRule.setDeleteFlag(ResourceAlarmConstants.NOT_DELETE);
                resourceAlarmRule.setUserId(userInfo.getId());
                resourceAlarmRule.setTenantId(userInfo.getTenantId());
                resourceAlarmRules.add(resourceAlarmRule);
            }
            resourceAlarmRuleService.saveBatch(resourceAlarmRules);
        }
    }

    @Override
    public List<ResourceAlarmRuleVO> queryResourceAlarmRule() {
        // 每个用户针对不同资源的告警规则只能设置一种
        List<ResourceAlarmRule> dbResourceAlarmRule = resourceAlarmRuleMapper.selectList(new LambdaQueryWrapper<>(ResourceAlarmRule.class)
                .eq(ResourceAlarmRule::getUserId, UserUtils.getCurrentUserInfo().getId())
                .eq(ResourceAlarmRule::getDeleteFlag, ResourceAlarmConstants.NOT_DELETE)
        );
        if (CollUtil.isEmpty(dbResourceAlarmRule)) {
            // 如果一开始没有设置告警规则，则使用默认告警规则
            dbResourceAlarmRule = resourceAlarmRuleMapper.selectList(new LambdaQueryWrapper<>(ResourceAlarmRule.class).eq(ResourceAlarmRule::getUserId, ResourceAlarmConstants.DEFAULT_USER_ID));
        }
        return dbResourceAlarmRule.stream().map(resourceAlarmRule -> BeanUtil.copyProperties(resourceAlarmRule, ResourceAlarmRuleVO.class)).collect(Collectors.toList());
    }

    @Override
    public List<ResourceAlarmRule> queryAllResourceAlarmRule() {
        return resourceAlarmRuleMapper.selectList(null);
    }
}
