package com.ral.young.night.kafka;

import com.ral.young.night.kafka.config.KafkaConfig;
import com.ral.young.night.kafka.service.KafkaService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author renyunhui
 * @date 2024-06-24 14:04
 * @since 1.0.0
 */
public class KafkaApplication {

    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(KafkaConfig.class);
        applicationContext.refresh();

        KafkaService kafkaService = applicationContext.getBean(KafkaService.class);
        kafkaService.sendMessage("spring-topic", "hello world");

        Thread.sleep(2000);
        applicationContext.close();
    }
}
