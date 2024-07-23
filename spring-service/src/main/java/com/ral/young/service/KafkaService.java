package com.ral.young.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Resource;

/**
 *  kafka 服务
 *
 * @author renyunhui
 * @date 2024-07-23 16:04
 * @since 1.0.0
 */
@Service
@Slf4j
public class KafkaService {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String message, Long messageId) {
        String data = messageId + "@@" + message;
        kafkaTemplate.send(topic, data).addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("send message error , messageId : {} error : ", messageId, ex);
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("send message success , messageId : {}", messageId);
            }
        });
    }
}
