package com.ral.young.service;

import cn.hutool.json.JSONUtil;
import com.ral.young.po.ResourceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.ral.young.service.impl.ResourceInfoServiceImpl.RESOURCE_STATUS_SUCCESS;
import static com.ral.young.service.impl.ResourceInfoServiceImpl.TOPIC_NAME;

/**
 * 模拟当前服务的下游服务
 *
 * @author renyunhui
 * @date 2024-07-23 16:12
 * @since 1.0.0
 */
@Service
@Slf4j
public class TestService {

    @Resource
    private IResourceMessageService resourceMessageService;

    @KafkaListener(topics = {TOPIC_NAME}, groupId = "spring-group")
    public void receiveMessage(List<ConsumerRecord<String, String>> consumerRecords, Acknowledgment ack) {
        for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
            log.info("接收到消息：value：{}, partition：{}", consumerRecord.value(), consumerRecord.partition());
            dealResource(consumerRecord.value());
        }
        // 处理完成之后手动提交 ack
        ack.acknowledge();
    }

    public void dealResource(String kafkaData) {
        log.info("处理资源：{}", kafkaData);
        String[] split = kafkaData.split("@@");
        long messageId = Long.parseLong(split[0]);
        ResourceInfo resourceInfo = JSONUtil.toBean(split[1], ResourceInfo.class);
        // todo 幂等性处理
        // 如果已经处理过了，需要回调接口修改本地消息表的执行状态
        if (null != resourceInfo) {
            log.info("本次需要处理的消息id：{}，资源信息：{}", messageId, split[1]);
            // 模拟处理业务逻辑
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("中断异常");
            }

            // todo 回调接口修改本地消息表的执行状态  目前是本地模拟
            int result = resourceMessageService.updateStatus(messageId, RESOURCE_STATUS_SUCCESS);
            log.info("处理完成 : {}", result);
        }
    }
}
