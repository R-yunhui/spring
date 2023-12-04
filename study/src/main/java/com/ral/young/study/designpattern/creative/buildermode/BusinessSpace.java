package com.ral.young.study.designpattern.creative.buildermode;

import lombok.Getter;

/**
 * 业务空间 - 类比建造者模式的产品 Product
 * 产品(Product)：要创建的产品对象
 *
 * @author renyunhui
 * @date 2023-12-04 9:40
 * @since 1.0.0
 */
@Getter
public class BusinessSpace {

    private String spaceName;

    private Object templateA;

    private Object templateB;

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public void setTemplateA(Object templateA) {
        this.templateA = templateA;
    }

    public void setTemplateB(Object templateB) {
        this.templateB = templateB;
    }
}
