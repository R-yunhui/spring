package com.ral.young.boot.service.impl;

import cn.hutool.core.util.IdUtil;
import com.ral.young.boot.bo.Goods;
import com.ral.young.boot.service.IGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品信息接口
 *
 * @author renyunhui
 * @date 2022-08-03 9:39
 * @since 1.0.0
 */
@Service
@Slf4j
public class GoodsServiceImpl implements IGoodsService {

    @Override
    public List<Goods> queryAllGoods() {
        log.info("开始查询全量商品信息");
        // 模拟数据库查询的情况
        int size = 500;
        List<Goods> goods = new ArrayList<>(550);
        for (int i = 0; i < size; i++) {
            goods.add(Goods.builder().id((long) (i + 1)).goodsName(IdUtil.fastSimpleUUID()).build());
        }
        return goods;
    }
}
