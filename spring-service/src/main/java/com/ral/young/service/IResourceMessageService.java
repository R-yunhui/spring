package com.ral.young.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ral.young.po.ResourceMessage;

import java.util.List;

/**
 * 资源本地消息表服务类
 *
 * @author renyunhui
 * @date 2024-07-23 15:39
 * @since 1.0.0
 */
public interface IResourceMessageService extends IService<ResourceMessage> {

    /**
     * 根据消息id 修改消息状态
     * @param id 消息id
     * @param status 状态
     * @return 修改后的状态结果
     */
    int updateStatus(Long id, Byte status);

    /**
     * 根据消息状态查询消息信息
     * @param status 消息状态
     * @return 符合条件的消息数据
     */
    List<ResourceMessage> queryMessageByStatus(Byte status);
}
