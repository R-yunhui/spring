package com.ral.young.spring.basic.handler;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;

import java.util.List;

/**
 * @author renyunhui
 * @description 扩展 mp 的批量插入的方法
 * @date 2024-11-21 14-09-07
 * @since 1.0.0
 */
public class ExpandSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        // 增加 mysql 批量插入的扩展方法，并且不需要使用逻辑删除字段
        methodList.add(new InsertBatchSomeColumn(t -> !t.isLogicDelete()));
        return methodList;
    }
}
