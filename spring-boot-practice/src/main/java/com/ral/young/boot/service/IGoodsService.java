package com.ral.young.boot.service;

import com.ral.young.boot.bo.Goods;

import java.util.List;

/**
 * @author renyunhui
 * @date 2022-08-03 9:34
 * @since 1.0.0
 */
public interface IGoodsService {

    /**
     * 查询所有商品信息
     *
     * @return 查询到的商品信息
     */
    List<Goods> queryAllGoods();
}
