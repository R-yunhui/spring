package com.ral.young.boot.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 商品信息
 *
 * @author renyunhui
 * @date 2022-08-03 9:35
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Goods {

    private Long id;

    private String goodsName;

    private String goodsDescription;

    private double goodsPrice;

    private Date productionDate;

    private int shelfLife;

    private byte[] bytes = new byte[1024];
}
