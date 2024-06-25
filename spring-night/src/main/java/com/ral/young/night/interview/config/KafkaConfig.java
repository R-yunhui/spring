package com.ral.young.night.interview.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * kafka 配置类
 *
 * @author renyunhui
 * @date 2024-06-25 9:37
 * @since 1.0.0
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public KafkaProperties kafkaProperties() {
        KafkaProperties kafkaProperties = new KafkaProperties();
        kafkaProperties.setBootstrapServers(Collections.singletonList("101.43.7.180:9092"));
        KafkaProperties.Producer producer = kafkaProperties.getProducer();
        // 设置 ack = -1，标识 kafka 分区的 leader 以及 ISR 列表中的 follower 都接收到数据并写入到本地日志才算成功，消息丢失的可能性最低
        // ack = 0 表示不进行 ack，即不保证消息不丢失
        // ack = 1 表示 kafka 分区的 leader 接收到消息并写入消息到本地日志才算成功，折中的一种方式
        producer.setAcks("1");
        // 设置重试次数
        producer.setRetries(3);
        // 消息发送超时或者失败的重试间隔时间
        producer.getProperties().put(CommonClientConfigs.RETRY_BACKOFF_MS_CONFIG, "300");
        // 设置 key value 序列化器
        producer.setKeySerializer(StringSerializer.class);
        producer.setValueSerializer(StringSerializer.class);
        producer.setBootstrapServers(kafkaProperties.getBootstrapServers());

        KafkaProperties.Consumer consumer = kafkaProperties.getConsumer();
        // 关闭自动提交
        consumer.setEnableAutoCommit(false);
        // 设置 key value 反序列化器
        consumer.setKeyDeserializer(StringDeserializer.class);
        consumer.setValueDeserializer(StringDeserializer.class);
        consumer.setBootstrapServers(kafkaProperties.getBootstrapServers());
        return kafkaProperties;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        Map<String, Object> producerProperties = kafkaProperties().getProducer().buildProperties();
        DefaultKafkaProducerFactory<String, String> defaultKafkaProducerFactory = new DefaultKafkaProducerFactory<>(producerProperties);
        return new KafkaTemplate<>(defaultKafkaProducerFactory);
    }

    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(kafkaProperties().getConsumer().buildProperties()));
        // 并发度为 3
        factory.setConcurrency(3);
        // 设置为批量消费，每个批次数量在 Kafka配置参数中设置ConsumerConfig.MAX_POLL_RECORDS_CONFIG
        factory.setBatchListener(true);
        // set the retry template
        // factory.setRetryTemplate(retryTemplate());
        // 设置手动提交
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        //创建一个kafka管理类，相当于rabbitMQ的管理类rabbitAdmin,没有此bean无法自定义的使用 adminClient 创建topic
        Map<String, Object> props = new HashMap<>();
        // 配置Kafka实例的连接地址
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "101.43.7.180:9092");
        return new KafkaAdmin(props);
    }

    @Bean
    public AdminClient adminClient() {
        // kafka客户端，在spring中创建这个bean之后可以注入并且创建 topic
        return AdminClient.create(kafkaAdmin().getConfig());
    }
}
