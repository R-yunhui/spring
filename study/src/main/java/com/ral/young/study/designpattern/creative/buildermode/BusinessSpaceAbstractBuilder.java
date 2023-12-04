package com.ral.young.study.designpattern.creative.buildermode;

/**
 * 建造者的抽象类
 * 抽象建造者(Builder)：建造者的抽象类，规范产品对象的各个组成部分的建造，一般由子类实现具体建造过程
 *
 * @author renyunhui
 * @date 2023-12-04 9:42
 * @since 1.0.0
 */
public abstract class BusinessSpaceAbstractBuilder {

    public abstract BusinessSpaceAbstractBuilder buildName(String spaceName);

    public abstract BusinessSpaceAbstractBuilder buildTemplateA(Object templateA);

    public abstract BusinessSpaceAbstractBuilder buildTemplateB(Object templateB);

    public abstract BusinessSpace build();
}
