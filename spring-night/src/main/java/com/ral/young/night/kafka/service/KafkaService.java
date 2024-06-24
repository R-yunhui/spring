package com.ral.young.night.kafka.service;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 发送 kafka 消息
 *
 * @author renyunhui
 * @date 2024-06-24 15:21
 * @since 1.0.0
 */
@Service
@Slf4j
public class KafkaService {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource
    private AdminClient adminClient;

    public void sendMessage(String topic, String message) {
        try {
            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);

            // 等待注册的回调函数的返回值，同步等待结果
            SendResult<String, String> result = future.get();
            log.info("向 kafka topic：{} 发送消息成功，结果：{}", topic, result);
        } catch (Exception e) {
            log.error("向 kafka 发送消息异常，error：", e);
        }
    }

    @KafkaListener(topics = {"spring-topic"}, groupId = "spring-group")
    public void receiveMessage(List<ConsumerRecord<String, String>> consumerRecords, Acknowledgment ack) {
        consumerRecords.forEach(consumerRecord -> {
            log.info("接收到消息：value：{}, partition：{}", consumerRecord.value(), consumerRecord.partition());
        });
        // 处理完成之后手动提交 ack
        ack.acknowledge();
    }

    @PostConstruct
    public void init() {
        ListTopicsResult listTopics = adminClient.listTopics();
        AtomicBoolean isExist = new AtomicBoolean(false);
        String topicName = "spring-topic";
        listTopics.names().whenComplete((names, throwable) -> {
            if (throwable != null) {
                log.error("获取 kafka topic 失败，error：", throwable);
            } else {
                log.info("获取 kafka topic 成功，结果：{}", names);
                if (names.contains(topicName)) {
                    isExist.set(true);
                }

                // topic 不存在即创建一个
                if (!isExist.get()) {
                    // 分区数为 3，副本数为 1
                    NewTopic newTopic = new NewTopic(topicName, 3, (short) 1);
                    CreateTopicsResult result = adminClient.createTopics(Lists.newArrayList(newTopic));
                    result.values().forEach((k, v) -> {
                        v.whenComplete((r, t) -> {
                            if (t != null) {
                                log.error("创建 topic 失败，error：", t);
                            } else {
                                log.info("创建 topic：{} 成功，结果：{}", topicName, r);
                            }
                        });
                    });
                }
            }
        });
    }
}
