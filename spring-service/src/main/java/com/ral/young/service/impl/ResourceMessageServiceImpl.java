package com.ral.young.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ral.young.mapper.ResourceMessageMapper;
import com.ral.young.po.ResourceMessage;
import com.ral.young.service.IResourceMessageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 资源服务实现类
 *
 * @author renyunhui
 * @date 2024-07-23 15:40
 * @since 1.0.0
 */
@Service(value = "resourceMessageService")
public class ResourceMessageServiceImpl extends ServiceImpl<ResourceMessageMapper, ResourceMessage> implements IResourceMessageService {

    @Resource
    private ResourceMessageMapper resourceMessageMapper;

    @Override
    public int updateStatus(Long id, Byte status) {
        UpdateWrapper<ResourceMessage> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("status", status);
        return resourceMessageMapper.update(null, updateWrapper);
    }

    @Override
    public List<ResourceMessage> queryMessageByStatus(Byte status) {
        QueryWrapper<ResourceMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", status);
        return resourceMessageMapper.selectList(queryWrapper);
    }
}
