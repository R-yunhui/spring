package com.ral.young.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ral.young.bo.ResourceAlarmMessage;
import com.ral.young.enums.ResourceEnum;
import com.ral.young.mapper.ResourceAlarmMessageMapper;
import com.ral.young.service.ResourceAlarmMessageService;
import com.ral.young.vo.ResourceAlarmMessageVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author renyunhui
 * @description 这是一个ResourceAlarmMessageServiceImpl类
 * @date 2024-09-19 17-23-48
 * @since 1.2.0
 */
@Service
public class ResourceAlarmMessageServiceImpl extends ServiceImpl<ResourceAlarmMessageMapper, ResourceAlarmMessage> implements ResourceAlarmMessageService {

    @Resource
    private ResourceAlarmMessageMapper resourceAlarmMessageMapper;

    @Override
    public List<ResourceAlarmMessageVO> queryResourceAlarmMessage() {
        Long tenantId = 0L;
        List<ResourceAlarmMessage> dbResourceAlarmMessages = queryResourceAlarmMessageByTenantId(tenantId,
                DateUtil.beginOfDay(DateUtil.date()).getTime(), DateUtil.endOfDay(DateUtil.date()).getTime());

        if (CollUtil.isNotEmpty(dbResourceAlarmMessages)) {
            // 同一天同一租户产生的同一类型的告警需要进行合并
            List<ResourceAlarmMessage> resourceAlarmMessages = deduplicateResourceAlarmMessage(dbResourceAlarmMessages);
            return resourceAlarmMessages.stream().map(resourceAlarmMessage -> BeanUtil.copyProperties(resourceAlarmMessage, ResourceAlarmMessageVO.class)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public static List<ResourceAlarmMessage> deduplicateResourceAlarmMessage(List<ResourceAlarmMessage> resourceAlarmMessages) {
        Map<ResourceEnum, ResourceAlarmMessage> resourceAlarmMessageMap = new HashMap<>();

        for (ResourceAlarmMessage resourceAlarmMessage : resourceAlarmMessages) {
            ResourceEnum resourceEnum = resourceAlarmMessage.getResourceEnum();

            // 如果是第一次看到该类型，则直接添加
            if (!resourceAlarmMessageMap.containsKey(resourceEnum)) {
                resourceAlarmMessageMap.put(resourceEnum, resourceAlarmMessage);
            } else {
                ResourceAlarmMessage exist = resourceAlarmMessageMap.get(resourceEnum);
                Date alarmTime = exist.getAlarmTime();
                // 比较日期大小，保留较大的日期对应的数据
                if (DateUtil.between(alarmTime, resourceAlarmMessage.getAlarmTime(), DateUnit.SECOND) < 0) {
                    resourceAlarmMessageMap.put(resourceEnum, resourceAlarmMessage);
                }
            }
        }

        // 将结果转换为列表
        return new ArrayList<>(resourceAlarmMessageMap.values());
    }

    @Override
    public List<ResourceAlarmMessage> queryResourceAlarmMessageByTenantId(Long tenantId, Long start, Long end) {
        return resourceAlarmMessageMapper.selectList(new LambdaQueryWrapper<ResourceAlarmMessage>()
                .eq(ResourceAlarmMessage::getTenantId, tenantId)
                .between(ResourceAlarmMessage::getAlarmTime, start, end)
        );
    }

    @Override
    public Boolean deleteResourceAlarmMessage(List<Long> ids) {
        int count = resourceAlarmMessageMapper.update(null, new LambdaUpdateWrapper<ResourceAlarmMessage>()
                .set(ResourceAlarmMessage::getDeleteFlag, 1)
                .in(ResourceAlarmMessage::getId, ids));
        return count > 0;
    }

    @Override
    public void createResourceAlarmMessage(ResourceEnum resourceEnum, Long tenantId, Long ruleId, Double threshold) {
        String alarmMsg = String.format(resourceEnum.getAlarmMessage(), threshold);
        ResourceAlarmMessage resourceAlarmMessage = ResourceAlarmMessage.builder()
                .message(alarmMsg)
                .resourceEnum(resourceEnum)
                .ruleId(ruleId)
                .tenantId(tenantId)
                .alarmTime(DateUtil.date())
                .build();
        resourceAlarmMessageMapper.insert(resourceAlarmMessage);
    }

    @Override
    public void createResourceAlarmMessage(ResourceEnum resourceEnum, Long tenantId, Long ruleId) {
        String alarmMsg = resourceEnum.getAlarmMessage();
        ResourceAlarmMessage resourceAlarmMessage = ResourceAlarmMessage.builder()
                .message(alarmMsg)
                .resourceEnum(resourceEnum)
                .ruleId(ruleId)
                .tenantId(tenantId)
                .alarmTime(DateUtil.date())
                .build();
        resourceAlarmMessageMapper.insert(resourceAlarmMessage);
    }
}
