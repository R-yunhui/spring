package com.ral.young.task;

import com.ral.young.po.ResourceMessage;
import com.ral.young.service.IResourceMessageService;
import com.ral.young.service.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static com.ral.young.service.impl.ResourceInfoServiceImpl.RESOURCE_STATUS_INIT;
import static com.ral.young.service.impl.ResourceInfoServiceImpl.TOPIC_NAME;

/**
 * 定时任务扫本地消息表，发送到 kafka
 *
 * @author renyunhui
 * @date 2024-07-23 15:49
 * @since 1.0.0
 */
@Component
@Slf4j
public class TaskHandler {

    @Resource
    private KafkaService kafkaService;

    @Resource
    private IResourceMessageService resourceMessageService;

    @Scheduled(fixedRate = 1000 * 60)
    public void sendMessage() {
        List<ResourceMessage> resourceMessages = resourceMessageService.queryMessageByStatus(RESOURCE_STATUS_INIT);
        if (CollectionUtils.isNotEmpty(resourceMessages)) {
            log.info("需要发送的消息数量：{}", resourceMessages.size());
            resourceMessages.forEach(resourceMessage -> kafkaService.sendMessage(TOPIC_NAME, resourceMessage.getMessageInfo(), resourceMessage.getId()));
        } else {
            log.info("没有需要发送的消息");
        }
    }
}
