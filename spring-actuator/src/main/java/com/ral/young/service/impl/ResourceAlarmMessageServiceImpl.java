package com.ral.young.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
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
import java.util.Collections;
import java.util.List;
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
        List<ResourceAlarmMessage> resourceAlarmMessages = resourceAlarmMessageMapper.selectList(new LambdaQueryWrapper<ResourceAlarmMessage>().eq(ResourceAlarmMessage::getTenantId, tenantId));
        if (CollUtil.isNotEmpty(resourceAlarmMessages)) {
            return resourceAlarmMessages.stream().map(resourceAlarmMessage -> BeanUtil.copyProperties(resourceAlarmMessage, ResourceAlarmMessageVO.class)).collect(Collectors.toList());
        }
        return Collections.emptyList();
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
