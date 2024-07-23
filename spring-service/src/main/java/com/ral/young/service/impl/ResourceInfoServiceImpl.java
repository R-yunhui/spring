package com.ral.young.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ral.young.mapper.ResourceMapper;
import com.ral.young.mapper.ResourceMessageMapper;
import com.ral.young.po.ResourceInfo;
import com.ral.young.po.ResourceMessage;
import com.ral.young.service.IResourceInfoService;
import com.ral.young.service.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

/**
 * 资源服务实现类
 *
 * @author renyunhui
 * @date 2024-07-23 15:40
 * @since 1.0.0
 */
@Service(value = "resourceInfoService")
@Slf4j
public class ResourceInfoServiceImpl extends ServiceImpl<ResourceMapper, ResourceInfo> implements IResourceInfoService {

    @Resource
    private ResourceMapper resourceMapper;

    @Resource
    private ResourceMessageMapper resourceMessageMapper;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private KafkaService kafkaService;

    public static final byte RESOURCE_STATUS_INIT = 0;

    public static final byte RESOURCE_STATUS_SUCCESS = 1;

    public static final String TOPIC_NAME = "spring-topic";

    @Override
    public int createResource(ResourceInfo resourceInfo) {
        ResourceMessage resourceMessage = new ResourceMessage();
        long id = IdUtil.getSnowflakeNextId();
        resourceMessage.setId(id);
        resourceMessage.setStatus(RESOURCE_STATUS_INIT);
        String data = JSONUtil.toJsonPrettyStr(resourceInfo);
        resourceMessage.setMessageInfo(data);

        // 由此事务保证本服务业务逻辑执行正常，并且存储本地消息表数据支持
        Boolean execute = transactionTemplate.execute(transactionStatus -> {
            try {
                resourceMapper.insert(resourceInfo);
                resourceMessageMapper.insert(resourceMessage);
                return true;
            } catch (Exception e) {
                log.error("存储资源信息或本地消息表数据失败，error：", e);
                return false;
            }
        });

        if (Boolean.TRUE.equals(execute)) {
            sendResourceCreatedMessage(data, id);
            return 0;
        }

        log.error("存储资源信息或本地消息表数据失败");
        return -1;
    }

    private void sendResourceCreatedMessage(String data, long id) {
        try {
            kafkaService.sendMessage(TOPIC_NAME, data, id);
        } catch (Exception e) {
            log.error("发送资源创建消息失败，topic: {}, resourceId: {}, error：", TOPIC_NAME, id, e);
            // 考虑是否需要进一步处理发送消息失败的情况
        }
    }
}
