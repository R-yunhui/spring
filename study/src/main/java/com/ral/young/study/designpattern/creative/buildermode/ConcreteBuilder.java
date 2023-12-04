package com.ral.young.study.designpattern.creative.buildermode;

/**
 * 实际的建造者角色
 * 建造者(ConcreteBuilder)：具体的Builder类，根据不同的业务逻辑，具体到各个对象的各个组成部分的建造
 *
 * @author renyunhui
 * @date 2023-12-04 9:44
 * @since 1.0.0
 */
public class ConcreteBuilder extends BusinessSpaceAbstractBuilder {

    private final BusinessSpace businessSpace;

    public ConcreteBuilder() {
        this.businessSpace = new BusinessSpace();
    }

    public ConcreteBuilder(BusinessSpace businessSpace) {
        this.businessSpace = businessSpace;
    }

    @Override
    public BusinessSpaceAbstractBuilder buildName(String spaceName) {
        businessSpace.setSpaceName(spaceName);
        return this;
    }

    @Override
    public BusinessSpaceAbstractBuilder buildTemplateA(Object templateA) {
        businessSpace.setTemplateA(templateA);
        return this;
    }

    @Override
    public BusinessSpaceAbstractBuilder buildTemplateB(Object templateB) {
        businessSpace.setTemplateB(templateB);
        return this;
    }

    public BusinessSpace build() {
        return businessSpace;
    }
}
