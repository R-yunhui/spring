# SpringCloud Sentinel

## @ResoureSetinel 工作原理

**com.alibaba.cloud.sentinel.custom.SentinelAutoConfiguration**

**com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect  这个Bean标记了`@SentinelResource` 注解的切面**

```java
@Aspect
public class SentinelResourceAspect extends AbstractSentinelAspectSupport {

    @Pointcut("@annotation(com.alibaba.csp.sentinel.annotation.SentinelResource)")
    public void sentinelResourceAnnotationPointcut() {
    }

    @Around("sentinelResourceAnnotationPointcut()")
    public Object invokeResourceWithSentinel(ProceedingJoinPoint pjp) throws Throwable {
        Method originMethod = resolveMethod(pjp);

        // 获取注解 @SentinelResource 的信息
        SentinelResource annotation = originMethod.getAnnotation(SentinelResource.class);
        if (annotation == null) {
            // Should not go through here.
            throw new IllegalStateException("Wrong state for SentinelResource annotation");
        }
        // 获取资源名称 
        // @SentinelResource(value = "saySomething")
        String resourceName = getResourceName(annotation.value(), originMethod);
        EntryType entryType = annotation.entryType();
        int resourceType = annotation.resourceType();
        Entry entry = null;
        try {
            // // 执行 entry
            entry = SphU.entry(resourceName, resourceType, entryType, pjp.getArgs());
            
            // 执行业务方法
            Object result = pjp.proceed();
            return result;
        } catch (BlockException ex) {
            // 处理 BlockException
            return handleBlockException(pjp, annotation, ex);
        } catch (Throwable ex) {
            Class<? extends Throwable>[] exceptionsToIgnore = annotation.exceptionsToIgnore();
            // The ignore list will be checked first.
            if (exceptionsToIgnore.length > 0 && exceptionBelongsTo(ex, exceptionsToIgnore)) {
                throw ex;
            }
            if (exceptionBelongsTo(ex, annotation.exceptionsToTrace())) {
                traceException(ex);
                // 处理降级
                return handleFallback(pjp, annotation, ex);
            }

            // No fallback function can handle the exception, so throw it out.
            throw ex;
        } finally {
            if (entry != null) {
                entry.exit(1, pjp.getArgs());
            }
        }
    }
}

```

#### 执行过程

1. 通过`Aop`进行拦截标记了`@SentinelResource` 的资源
2. 通过`SphU.entry`执行对应的流控规则（核心）
3. 调用业务方法
4. 对应的异常处理



## 责任链模式处理流控

### 责任链初始化过程

**`com.alibaba.csp.sentinel.CtSph#entryWithPriority`**

```java
    private Entry entryWithPriority(ResourceWrapper resourceWrapper, int count, boolean prioritized, Object... args)
        throws BlockException {
        // 省略部分代码.....

        // 初始化责任链
        ProcessorSlot<Object> chain = lookProcessChain(resourceWrapper);

        // 省略部分代码.....
        
        try {
            // 执行 entry
            chain.entry(context, resourceWrapper, null, count, prioritized, args);
        } catch (BlockException e1) {
            // 异常抛出,SentinelResourceAspect.invokeResourceWithSentinel 统一处理
            e.exit(count, args);
            throw e1;
        } catch (Throwable e1) {
            // This should not happen, unless there are errors existing in Sentinel internal.
            RecordLog.info("Sentinel unexpected exception", e1);
        }
        return e;
    }
```



**`com.alibaba.csp.sentinel.CtSph#lookProcessChain`**

```java
    ProcessorSlot<Object> lookProcessChain(ResourceWrapper resourceWrapper) {
        ProcessorSlotChain chain = chainMap.get(resourceWrapper);
        if (chain == null) {
            synchronized (LOCK) {
                chain = chainMap.get(resourceWrapper);
                if (chain == null) {
                    // Entry size limit.
                    // ProcessorSlot总计数不得超过Constants.MAX_SLOT_CHAIN_SIZE ，否则将返回null。
                    if (chainMap.size() >= Constants.MAX_SLOT_CHAIN_SIZE) {
                        return null;
                    }

                    // 构建的责任链
                    chain = SlotChainProvider.newSlotChain();
                    Map<ResourceWrapper, ProcessorSlotChain> newMap = new HashMap<ResourceWrapper, ProcessorSlotChain>(
                        chainMap.size() + 1);
                    newMap.putAll(chainMap);
                    newMap.put(resourceWrapper, chain);
                    chainMap = newMap;
                }
            }
        }
        return chain;
    }
```

![image-20210528105306715](http://ren-bed.oss-cn-beijing.aliyuncs.com/img/image-20210528105306715.png)



**`com.alibaba.csp.sentinel.slotchain.SlotChainProvider#newSlotChain`**

```java
public final class SlotChainProvider {

    private static volatile SlotChainBuilder slotChainBuilder = null;

    public static ProcessorSlotChain newSlotChain() {
        if (slotChainBuilder != null) {
            return slotChainBuilder.build();
        }

        // Resolve the slot chain builder SPI.
        // 通过SPI机制加载所有 SlotChainBuilder的实现
        slotChainBuilder = SpiLoader.loadFirstInstanceOrDefault(SlotChainBuilder.class, DefaultSlotChainBuilder.class);

        if (slotChainBuilder == null) {
            // Should not go through here.
            RecordLog.warn("[SlotChainProvider] Wrong state when resolving slot chain builder, using default");
            slotChainBuilder = new DefaultSlotChainBuilder();
        } else {
            RecordLog.info("[SlotChainProvider] Global slot chain builder resolved: "
                + slotChainBuilder.getClass().getCanonicalName());
        }
        // 构建所有流控规则的责任链 （从尾部添加）
        return slotChainBuilder.build();
    }

    private SlotChainProvider() {}
}
```



**`com.alibaba.csp.sentinel.slots.DefaultSlotChainBuilder#build`**

```java
public class DefaultSlotChainBuilder implements SlotChainBuilder {

    @Override
    public ProcessorSlotChain build() {
        ProcessorSlotChain chain = new DefaultProcessorSlotChain();
        chain.addLast(new NodeSelectorSlot());
        chain.addLast(new ClusterBuilderSlot());
        chain.addLast(new LogSlot());
        chain.addLast(new StatisticSlot());
        chain.addLast(new AuthoritySlot());
        chain.addLast(new SystemSlot());
        chain.addLast(new FlowSlot());
        chain.addLast(new DegradeSlot());

        return chain;
    }

}
```



### 责任链的执行过程

**责任链按顺序加载后的结果：**

1. NodeSelectorSolt
2. CusterBuilderSolt
3. LogSlot
4. StatisicSlot
5. AuthoritySolt
6. SystemSolts
7. ParamFlowSolt
8. FlowSolt
9. DegradeSlot



#### FlowSlot 流控

`FlowSlot` 会根据预设的规则，结合前面 `NodeSelectorSlot`、`ClusterBuilderSlot`、`StatisticSlot` 统计出来的实时信息进行流量控制。

**`com.alibaba.csp.sentinel.slots.block.flow.FlowSlot#entry`**

```java
    public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode node, int count,
                      boolean prioritized, Object... args) throws Throwable {
      	// 检查流量
        checkFlow(resourceWrapper, context, node, count, prioritized);

        fireEntry(context, resourceWrapper, node, count, prioritized, args);
    }
```



**核心：`com.alibaba.csp.sentinel.slots.block.flow.controller.DefaultController#canPass`**

```java
    @Override
    public boolean canPass(Node node, int acquireCount, boolean prioritized) {
        // 当前资源被调用过的次数
        int curCount = avgUsedTokens(node);
        // 当前资源调用次数 + 1 > 阈值
        if (curCount + acquireCount > count) {
            // 省略部分代码 
            // 不通过
            return false;
        }
        return true;
    }

    private int avgUsedTokens(Node node) {
        if (node == null) {
            return DEFAULT_AVG_USED_TOKENS;
        }
        return grade == RuleConstant.FLOW_GRADE_THREAD ? node.curThreadNum() : (int)(node.passQps());
    }
```



**如果返回不通过会回到，那么会抛出 `FlowException`**

**`com.alibaba.csp.sentinel.slots.block.flow.FlowRuleChecker#checkFlow`**

```java
    public void checkFlow(Function<String, Collection<FlowRule>> ruleProvider, ResourceWrapper resource,
                          Context context, DefaultNode node, int count, boolean prioritized) throws BlockException {
        if (ruleProvider == null || resource == null) {
            return;
        }
        Collection<FlowRule> rules = ruleProvider.apply(resource.getName());
        if (rules != null) {
            for (FlowRule rule : rules) {
                // 验证不通过
                if (!canPassCheck(rule, context, node, count, prioritized)) {
                    throw new FlowException(rule.getLimitApp(), rule);
                }
            }
        }
    }
```

然后会在 `com.alibaba.csp.sentinel.slots.statistic.StatisticSlot` 中增加统计信息， 最后会抛出给 `SentinelResourceAspect` 进行处理，完成流控功能。异常信息如果是`BlockException` 异常，会进入 `handleBlockException` 方法处理， 如果是其他的业务异常首先会判断是否有配置 `fallback` 处理如果有，就调用 `handleFallback` 没有就继续往外抛，至此完成流控功能。

