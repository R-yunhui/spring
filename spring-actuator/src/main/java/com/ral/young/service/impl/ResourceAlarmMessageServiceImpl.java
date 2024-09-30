package com.ral.young.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ral.young.bo.ResourceAlarmMessage;
import com.ral.young.enums.ResourceEnum;
import com.ral.young.mapper.ResourceAlarmMessageMapper;
import com.ral.young.service.ResourceAlarmMessageService;
import com.ral.young.vo.ResourceAlarmMessageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Resource
    private ResourceAlarmMessageService resourceAlarmMessageService;

    @Resource
    private WsMessageBroadcaster wsMessageBroadcaster;

    @Override
    public List<ResourceAlarmMessageVO> queryResourceAlarmMessage() {
        Long tenantId = UserUtils.getCurrentUserInfo().getTenantId();
        List<ResourceAlarmMessage> dbResourceAlarmMessages = resourceAlarmMessageMapper.selectList(new LambdaQueryWrapper<ResourceAlarmMessage>()
                .eq(ResourceAlarmMessage::getTenantId, tenantId).eq(ResourceAlarmMessage::getDeleteFlag, ResourceAlarmConstants.NOT_DELETE)
                .eq(ResourceAlarmMessage::getAlarmStatus, ResourceAlarmConstants.ALARM_MESSAGE)
                .between(ResourceAlarmMessage::getAlarmTime, DateUtil.beginOfDay(DateUtil.date()), DateUtil.endOfDay(DateUtil.date()))
                .orderByDesc(ResourceAlarmMessage::getAlarmTime)
        );
        return dbResourceAlarmMessages.stream().map(resourceAlarmMessage -> BeanUtil.copyProperties(resourceAlarmMessage, ResourceAlarmMessageVO.class)).collect(Collectors.toList());
    }

    @Override
    public void deleteResourceAlarmMessage(IdsDTO idsDTO) {
        resourceAlarmMessageMapper.update(null, new LambdaUpdateWrapper<ResourceAlarmMessage>()
                .set(ResourceAlarmMessage::getDeleteFlag, ResourceAlarmConstants.DELETE)
                .in(ResourceAlarmMessage::getId, idsDTO.getIds()));
    }

    @Override
    public void createAuthResourceAlarmMessage(ResourceEnum resourceEnum, Long tenantId, Byte alarm) {
        ResourceAlarmMessage resourceAlarmMessage = ResourceAlarmMessage.builder()
                .message(resourceEnum.getAlarmMessage()).resourceEnum(resourceEnum)
                // 认证过期的告警不存在规则id，默认传递 -1 即可
                .ruleId(ResourceAlarmConstants.DEFAULT_RULE_ID).userId(ResourceAlarmConstants.DEFAULT_USER_ID)
                .alarmStatus(alarm).tenantId(tenantId)
                .alarmTime(DateUtil.date())
                .build();
        // 租户级别告警，同一天统一租户只能产生一次这种告警，有新的就更新告警的时间和告警消息
        ResourceAlarmMessage dbAlarmMessage = resourceAlarmMessageMapper.selectOne(new LambdaQueryWrapper<ResourceAlarmMessage>()
                .eq(ResourceAlarmMessage::getTenantId, tenantId).eq(ResourceAlarmMessage::getResourceEnum, resourceEnum)
                .eq(ResourceAlarmMessage::getDeleteFlag, ResourceAlarmConstants.NOT_DELETE)
                .between(ResourceAlarmMessage::getAlarmTime, DateUtil.beginOfDay(DateUtil.date()), DateUtil.endOfDay(DateUtil.date()))
                .orderByDesc(ResourceAlarmMessage::getAlarmTime).last(" limit 1")
        );

        if (ObjectUtil.isNotNull(dbAlarmMessage)) {
            // 两次告警状态是否一致
            boolean updateDb = checkExistAlarm(resourceEnum, tenantId, resourceAlarmMessage, dbAlarmMessage);
            if (updateDb) {
                // 进行更新即可
                dbAlarmMessage.setAlarmStatus(resourceAlarmMessage.getAlarmStatus());
                resourceAlarmMessageMapper.update(null, new LambdaUpdateWrapper<ResourceAlarmMessage>()
                        .eq(ResourceAlarmMessage::getId, dbAlarmMessage.getId())
                        .set(ResourceAlarmMessage::getAlarmStatus, alarm));
            }
        } else if (ResourceAlarmConstants.ALARM_MESSAGE.equals(alarm)) {
            // 第一次产生告警需要推送 redis 同时入库
            wsMessageBroadcaster.broadcast(ResourceAlarmVO.builder()
                    .alarm(alarm).resourceEnum(resourceEnum)
                    .tenantId(tenantId).userId(ResourceAlarmConstants.DEFAULT_USER_ID)
                    .build());
            resourceAlarmMessageMapper.insert(resourceAlarmMessage);
        }
    }

    /**
     * 校验上一次产生的预警和本次的预警，按照规则判断是否需要更新告警信息以及推送websocket
     * 1.之前是告警，本次是非告警，需要更新恢复时间
     * 2.之前是非告警，本次是告警，需要更新告警时间和告警消息
     * 3.之前是告警本次也是告警，需要更新告警时间和告警消息
     *
     * @param resourceEnum         资源枚举
     * @param tenantId             租户id
     * @param resourceAlarmMessage 当前产生的告警信息
     * @param dbAlarmMessage       之前存在的告警消息
     * @return 是否需要更新数据库告警
     */
    private boolean checkExistAlarm(ResourceEnum resourceEnum, Long tenantId, ResourceAlarmMessage resourceAlarmMessage, ResourceAlarmMessage dbAlarmMessage) {
        // 参数校验
        if (resourceAlarmMessage == null || dbAlarmMessage == null) {
            throw new IllegalArgumentException("resourceAlarmMessage and dbAlarmMessage cannot be null");
        }

        boolean sendWs = !Objects.equals(dbAlarmMessage.getAlarmStatus(), resourceAlarmMessage.getAlarmStatus());
        boolean updateDb = false;
        if (ResourceAlarmConstants.ALARM_MESSAGE.equals(dbAlarmMessage.getAlarmStatus()) && ResourceAlarmConstants.NORMAL_MESSAGE.equals(resourceAlarmMessage.getAlarmStatus())) {
            // 1.之前是告警，本次是非告警，需要更新恢复时间
            dbAlarmMessage.setResumeTime(DateUtil.date());
            updateDb = true;
        } else if (ResourceAlarmConstants.NORMAL_MESSAGE.equals(dbAlarmMessage.getAlarmStatus()) && ResourceAlarmConstants.ALARM_MESSAGE.equals(resourceAlarmMessage.getAlarmStatus())) {
            // 2.之前是非告警，本次是告警，需要更新告警时间和告警消息
            dbAlarmMessage.setAlarmTime(resourceAlarmMessage.getAlarmTime());
            dbAlarmMessage.setMessage(resourceAlarmMessage.getMessage());
            updateDb = true;
        } else if (ResourceAlarmConstants.ALARM_MESSAGE.equals(dbAlarmMessage.getAlarmStatus()) && ResourceAlarmConstants.ALARM_MESSAGE.equals(resourceAlarmMessage.getAlarmStatus())) {
            // 3.之前是告警本次也是告警，需要更新告警时间和告警消息
            dbAlarmMessage.setAlarmTime(resourceAlarmMessage.getAlarmTime());
            dbAlarmMessage.setMessage(resourceAlarmMessage.getMessage());
            updateDb = true;
        }

        if (sendWs) {
            wsMessageBroadcaster.broadcast(ResourceAlarmVO.builder()
                    .alarm(resourceAlarmMessage.getAlarmStatus()).resourceEnum(resourceEnum)
                    .tenantId(tenantId).userId(resourceAlarmMessage.getUserId())
                    .build());
        }
        return updateDb;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generatedAlarmList(List<ResourceAlarmMessage> resourceAlarmMessageList) {
        try {
            List<Long> userIdList = resourceAlarmMessageList.stream().map(ResourceAlarmMessage::getUserId).collect(Collectors.toList());
            // 用户级别：同一天针对同一个用户和同一种类型的告警只允许产生一种告警，有新的就更新告警的时间和告警消息
            List<ResourceAlarmMessage> dbResourceAlarmMessages = resourceAlarmMessageMapper.selectList(new LambdaQueryWrapper<ResourceAlarmMessage>()
                    .in(ResourceAlarmMessage::getUserId, userIdList)
                    .eq(ResourceAlarmMessage::getDeleteFlag, ResourceAlarmConstants.NOT_DELETE)
                    .between(ResourceAlarmMessage::getAlarmTime, DateUtil.beginOfDay(DateUtil.date()), DateUtil.endOfDay(DateUtil.date()))
            );

            if (CollUtil.isNotEmpty(dbResourceAlarmMessages)) {
                List<ResourceAlarmMessage> addAlarmMsgList = new ArrayList<>();
                List<ResourceAlarmMessage> updateAlarmMsgList = new ArrayList<>();

                Map<String, ResourceAlarmMessage> dbResourceAlarmMessageMap = dbResourceAlarmMessages.stream()
                        // key：资源类型::用户id::节点名称
                        .collect(Collectors.toMap(o -> o.getResourceEnum().name() + "::" + o.getUserId() + "::" + o.getNodeName(), o -> o));
                for (ResourceAlarmMessage resourceAlarmMessage : resourceAlarmMessageList) {
                    ResourceEnum resourceEnum = resourceAlarmMessage.getResourceEnum();
                    Long tenantId = resourceAlarmMessage.getTenantId();
                    String key = resourceEnum.name() + "::" + resourceAlarmMessage.getUserId() + "::" + resourceAlarmMessage.getNodeName();
                    ResourceAlarmMessage dbAlarmMessage = dbResourceAlarmMessageMap.get(key);
                    // 找到该用户和该类型对应的告警
                    if (ObjectUtil.isNotNull(dbAlarmMessage)) {
                        // 两次告警状态是否一致
                        boolean updateDb = checkExistAlarm(resourceEnum, tenantId, resourceAlarmMessage, dbAlarmMessage);
                        if (updateDb) {
                            // 进行更新即可
                            dbAlarmMessage.setAlarmStatus(resourceAlarmMessage.getAlarmStatus());
                            updateAlarmMsgList.add(dbAlarmMessage);
                        }
                    } else if (ResourceAlarmConstants.ALARM_MESSAGE.equals(resourceAlarmMessage.getAlarmStatus())) {
                        // 如果是第一次看到该类型，则直接添加
                        addAlarmMsgList.add(resourceAlarmMessage);

                        // 新的告警数据需要直接通过 ws 推送消息
                        wsMessageBroadcaster.broadcast(ResourceAlarmVO.builder()
                                .alarm(resourceAlarmMessage.getAlarmStatus()).resourceEnum(resourceEnum)
                                .tenantId(tenantId).userId(resourceAlarmMessage.getUserId())
                                .build());
                    }
                }

                // 分别进行更新和新增即可
                if (CollUtil.isNotEmpty(addAlarmMsgList)) {
                    resourceAlarmMessageService.saveBatch(addAlarmMsgList);
                }

                if (CollUtil.isNotEmpty(updateAlarmMsgList)) {
                    resourceAlarmMessageService.updateBatchById(updateAlarmMsgList);
                }
                return;
            }

            // 需要过滤掉非告警的信息
            resourceAlarmMessageList = resourceAlarmMessageList.stream().filter(o -> ResourceAlarmConstants.ALARM_MESSAGE.equals(o.getAlarmStatus())).collect(Collectors.toList());
            if (CollUtil.isEmpty(resourceAlarmMessageList)) {
                log.info("本次产生的都是非告警数据，不进行入库");
                return;
            }

            // 推送 ws
            for (ResourceAlarmMessage resourceAlarmMessage : resourceAlarmMessageList) {
                wsMessageBroadcaster.broadcast(ResourceAlarmVO.builder()
                        .alarm(resourceAlarmMessage.getAlarmStatus()).resourceEnum(resourceAlarmMessage.getResourceEnum())
                        .tenantId(resourceAlarmMessage.getTenantId()).userId(resourceAlarmMessage.getUserId())
                        .build());
            }
            log.info("本次推送 ws 告警消息：{}条", resourceAlarmMessageList.size());
            resourceAlarmMessageService.saveBatch(resourceAlarmMessageList);
        } catch (Exception e) {
            log.error("产生资源告警信息异常，", e);
        }
    }
}
