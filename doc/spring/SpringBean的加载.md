### Spring Bean

**实际创建bean的方法**

**org.springframework.beans.fWactory.support.AbstractAutowireCapableBeanFactory#doCreateBean**

```java
protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final @Nullable Object[] args)
    throws BeanCreationException {
    // 省略非关键代码
  if (instanceWrapper == null) {
      // 实例化Bean 通过RootBeanDefinition反射后包装到BeanWarpper中
    instanceWrapper = createBeanInstance(beanName, mbd, args);
  }
    
  final Object bean = instanceWrapper.getWrappedInstance();
	// Eagerly cache singletons to be able to resolve circular references
	// even when triggered by lifecycle interfaces like BeanFactoryAware.
  boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
		isSingletonCurrentlyInCreation(beanName));
  if (earlySingletonExposure) {
	 if (logger.isTraceEnabled()) {
		 logger.trace("Eagerly caching bean '" + beanName +
					"' to allow for resolving potential circular references");
		}
        // 存入三级缓存
	addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
  }
    
    // 省略非关键代码
  Object exposedObject = bean;
  try {
       // 注入Bean依赖  属性装配 如果有返现属性调用了另外一个bean，则调用getBean()方法
       populateBean(beanName, mbd, instanceWrapper);
        // 初始化Bean  
        // 执行BeanPostProcessor.postProcessBeforeInitialization() -> InitializingBean.init()  ->    BeanPostProcessor.postProcessAfterInitialization
       exposedObject = initializeBean(beanName, exposedObject, mbd);
    }
    catch (Throwable ex) {
    // 省略非关键代码
}
```

上述代码完整地展示了 Bean 初始化的三个关键步骤，按执行顺序分别是第 5 行的 createBeanInstance，第 12 行的 populateBean，以及第 13 行的 initializeBean，分别对应实例化 Bean，注入 Bean 依赖，以及初始化 Bean （例如执行 @PostConstruct 标记的方法 ）这三个功能，这也和上述时序图的流程相符。



**org.springframework.beans.factory.support.DefaultSingletonBeanRegistry**

```java
// --- 三级缓存解决循环依赖

/** Cache of singleton objects: bean name to bean instance. */
// 一级缓存：存放初始化完成的bean 可以从缓存中取出直接使用
private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

/** Cache of singleton factories: bean name to ObjectFactory. */
// 三级缓存：存放bean工厂对象，用于解决循环依赖
private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

/** Cache of early singleton objects: bean name to bean instance. */
// 二级缓存：存放原始的bean对象（尚未填充属性），用于解决循环依赖
private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);
```



**org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(java.lang.String, boolean)**

```java
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
    // 从一级缓存查询
   Object singletonObject = this.singletonObjects.get(beanName);
    // 一级缓存为空且处于正在创建标识
   if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
      synchronized (this.singletonObjects) {
          // 一级缓存已存在则查询二级缓存
         singletonObject = this.earlySingletonObjects.get(beanName);
          // 二级缓存为空且允许创建早期应用
         if (singletonObject == null && allowEarlyReference) {
             // 从三级缓存获取（实例化后就存放）
            ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
            if (singletonFactory != null) {
                // 将三级缓存中的放入二级缓存，在移除三级缓存的
               singletonObject = singletonFactory.getObject();
               this.earlySingletonObjects.put(beanName, singletonObject);
               this.singletonFactories.remove(beanName);
            }
         }
      }
   }
   return singletonObject;
}
```



A 创建过程中需要 B，于是 A 将自己放到三级缓里面 ，去实例化 B

B 实例化的时候发现需要 A，于是 B 先查一级缓存，没有，再查二级缓存，还是没有，再查三级缓存，找到了！ 然后把三级缓存里面的这个 A 放到二级缓存里面，并删除三级缓存里面的 A 

B 顺利初始化完毕，将自己放到一级缓存里面（此时B里面的A依然是创建中状态） 然后回来接着创建 A，此时 B 已经创建结束，直接从一级缓存里面拿到 B ，然后完成自己的创建，并将自己放到一级缓存里面。



```java
	public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
		Assert.notNull(beanName, "Bean name must not be null");
		Assert.notNull(singletonObject, "Singleton object must not be null");
		synchronized (this.singletonObjects) {
			Object oldObject = this.singletonObjects.get(beanName);
			if (oldObject != null) {
				throw new IllegalStateException("Could not register object [" + singletonObject +
						"] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
			}
            // doCreateBean 之后调用，属性赋值玩的bean添加到一级缓存，可以直接使用的bean
			addSingleton(beanName, singletonObject);
		}
	}

	/**
	 * Add the given singleton object to the singleton cache of this factory.
	 * <p>To be called for eager registration of singletons.
	 * @param beanName the name of the bean
	 * @param singletonObject the singleton object
	 */
	protected void addSingleton(String beanName, Object singletonObject) {
		synchronized (this.singletonObjects) {
            // 添加到一级缓存
			this.singletonObjects.put(beanName, singletonObject);
            // 移除三级缓存
			this.singletonFactories.remove(beanName);
            // 移除二级缓存
			this.earlySingletonObjects.remove(beanName);
			this.registeredSingletons.add(beanName);
		}
	}
```



**@Async产生的循环依赖问题**

**org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor**

```java
/** org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor:是一个bean的后置处理器,在bean初始化的时候执行,返回的bean对象和在实例化后通过:org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#getEarlyBeanReference,获取到的bean对象不一致，导致出现的循环依赖报错 */
    
 protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final @Nullable Object[] args)
    throws BeanCreationException {
    // 省略非关键代码
  if (instanceWrapper == null) {
      // 实例化Bean 通过RootBeanDefinition反射后包装到BeanWarpper中
    instanceWrapper = createBeanInstance(beanName, mbd, args);
  }
    
  final Object bean = instanceWrapper.getWrappedInstance();
	// Eagerly cache singletons to be able to resolve circular references
	// even when triggered by lifecycle interfaces like BeanFactoryAware.
  boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
		isSingletonCurrentlyInCreation(beanName));
  if (earlySingletonExposure) {
	 if (logger.isTraceEnabled()) {
		 logger.trace("Eagerly caching bean '" + beanName +
					"' to allow for resolving potential circular references");
		}
        // 存入三级缓存
	addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
  }
    
    // 省略非关键代码
  Object exposedObject = bean;
  try {
       // 注入Bean依赖  属性装配 如果有返现属性调用了另外一个bean，则调用getBean()方法
       populateBean(beanName, mbd, instanceWrapper);
        // 初始化Bean  
        // 执行BeanPostProcessor.postProcessBeforeInitialization() -> InitializingBean.init()  ->    BeanPostProcessor.postProcessAfterInitialization
       exposedObject = initializeBean(beanName, exposedObject, mbd);
    }
    catch (Throwable ex) {
    // 省略非关键代码
        
   if (earlySingletonExposure) {
			Object earlySingletonReference = getSingleton(beanName, false);
			if (earlySingletonReference != null) {
				if (exposedObject == bean) {
					exposedObject = earlySingletonReference;
				}
				else if (!this.allowRawInjectionDespiteWrapping && hasDependentBean(beanName)) {
					String[] dependentBeans = getDependentBeans(beanName);
					Set<String> actualDependentBeans = new LinkedHashSet<>(dependentBeans.length);
					for (String dependentBean : dependentBeans) {
						if (!removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
							actualDependentBeans.add(dependentBean);
						}
					}
					if (!actualDependentBeans.isEmpty()) {
						throw new BeanCurrentlyInCreationException(beanName,
								"Bean with name '" + beanName + "' has been injected into other beans [" +
								StringUtils.collectionToCommaDelimitedString(actualDependentBeans) +
								"] in its raw version as part of a circular reference, but has eventually been " +
								"wrapped. This means that said other beans do not use the final version of the " +
								"bean. This is often the result of over-eager type matching - consider using " +
								"'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.");
				}
			}
		}
	}
        
        // 省略非关键代码
}
```

```java
	protected Object getEarlyBeanReference(String beanName, RootBeanDefinition mbd, Object bean) {
		Object exposedObject = bean;
		if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
			for (BeanPostProcessor bp : getBeanPostProcessors()) {
                // 通过@EnableAsync导入的后置处理器
          		// AsyncAnnotationBeanPostProcessor不是一个SmartInstantiationAwareBeanPostProcessor类型的
          		// 这就意味着即使我们通过AsyncAnnotationBeanPostProcessor创建了一个代理对象
         		// 但是早期暴露出去的用于给别的Bean进行注入的那个对象还是原始对象
         if (bp instanceof SmartInstantiationAwareBeanPostProcessor
				if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
					SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor) bp;
					exposedObject = ibp.getEarlyBeanReference(exposedObject, beanName);
				}
			}
		}
		return exposedObject;
	}
```

