package com.ral.young.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ral.young.po.ResourceInfo;

/**
 * 资源持久层接口
 *
 * @author renyunhui
 * @date 2024-07-23 15:39
 * @since 1.0.0
 */
public interface IResourceInfoService extends IService<ResourceInfo> {

    /**
     * 保存资源信息
     * @param resourceInfo 待保存的资源信息
     * @return 保存结果
     */
    int createResource(ResourceInfo resourceInfo);
}
