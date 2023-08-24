# Spring

## Spring IoC

**BeanFactory 是Spring底层的IoC容器**

**ApplicationContext 是具备应用特性的 BeanFactory 的超集**



### Dependency injection

- **setter注入**
- **constructor注入**
- **field注入**
- **auto-wiring**
- **method注入**



#### 注入方式

- **低依赖：构造器注入**
- **多依赖：Setter方法注入**
- **便利性：字段注入**
- **声明类：方法注入**
- **接口回调注入（并不是所有框架都存在）**



#### 限定注入

**使用注解 @Qualifier 限定**

- **通过 Bean名称 限定**
- **通过分组限定**

**基于注解 @Qualifier 扩展限定**

- **自定义注解 - 类似 SpringCloud 的 @LoadBalanced**



#### 延迟依赖注入

- **通过 ObjectProvider**
- **通过 ObjectFactory**



#### 依赖处理流程

- **入口：org.springframework.beans.factory.support.DefaultListableBeanFactory#resolveDependency**
- **依赖描述符：org.springframework.beans.factory.config.DependencyDescriptor**
- **自动绑定候选对象处理器：org.springframework.beans.factory.support.AutowireCandidateResolver**



#### @Autowired 注入过程

- **元信息解析**
- **依赖查找**
- **依赖注入（字段，方法）**

1. **构建元数据：org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#postProcessMergedBeanDefinition**
2. **元数据注入：org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#postProcessProperties**



#### 自定义依赖注入的注解

**org.springframework.context.annotation.AnnotationConfigUtils#registerAnnotationConfigProcessors(org.springframework.beans.factory.support.BeanDefinitionRegistry, java.lang.Object) 会注册一些Spring内置的BeanPostProcessor**

**注：当需要提前初始化或提前注册是时候，将其标记为static，注册的机制可以提前触发，不依赖于当前所在的bean**

```java
	/**
     * 当需要提前初始化或提前注册是时候，将其标记为static，注册的机制可以提前触发，不依赖于当前所在的bean
     * 直接替换默认的 AutowiredAnnotationBeanPostProcessor
     *
     * @return AutowiredAnnotationBeanPostProcessor
     */
    @Bean(name = AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME)
    @Order(Ordered.LOWEST_PRECEDENCE - 3)
    public static AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor() {
        AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
        Set<Class<? extends Annotation>> autowiredAnnotationTypes = new LinkedHashSet<>(4);
        autowiredAnnotationTypes.add(Autowired.class);
        autowiredAnnotationTypes.add(InjectedUser.class);
        autowiredAnnotationBeanPostProcessor.setAutowiredAnnotationTypes(autowiredAnnotationTypes);
        return autowiredAnnotationBeanPostProcessor;
    }

    /**
     * 当需要提前初始化或提前注册是时候，将其标记为static，注册的机制可以提前触发，不依赖于当前所在的bean
     * 在默认存在的 AutowiredAnnotationBeanPostProcessor 基础上增加自定义的依赖注解
     *
     * @return AutowiredAnnotationBeanPostProcessor
     */
    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE - 1)
    public static AutowiredAnnotationBeanPostProcessor myAutowiredAnnotationBeanPostProcessor() {
        AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
        Set<Class<? extends Annotation>> autowiredAnnotationTypes = new LinkedHashSet<>(4);
        autowiredAnnotationTypes.add(InjectedUser.class);
        autowiredAnnotationBeanPostProcessor.setAutowiredAnnotationTypes(autowiredAnnotationTypes);
        return autowiredAnnotationBeanPostProcessor;
    }
```



### 依赖来源

#### 依赖查找

- **Spring BeanDefinition -> org.springframework.beans.factory.support.DefaultListableBeanFactory#registerBeanDefinition**
- **Singleton  单例对象 -> org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#registerSingleton**



#### 依赖注入

- **Spring BeanDefinition -> org.springframework.beans.factory.support.DefaultListableBeanFactory#registerBeanDefinition**
- **Singleton  单例对象 -> org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#registerSingleton**
- **ResolvableDependency**
- **@Value的外部化配置**



**单例对象可以在IOC容器启动之后进行注册吗？**

**可以，单例对象的注册和BeanDefinition不同，BeanDefinition的注册会受到 org.springframework.beans.factory.support.DefaultListableBeanFactory#freezeConfiguration 的影响**

**单例对象则没这个限制**



#### 依赖对象

| 来源                                            | SpringBean 对象 | 生命周期管理 | 配置元信息 | 使用场景           |
| :---------------------------------------------- | --------------- | ------------ | ---------- | ------------------ |
| Spring BeanDefinition 对象                      | Y               | Y            | Y          | 依赖注入，依赖查找 |
| 单体对象 (非Spring容器管理的对象)               | Y               | N            | N          | 依赖注入，依赖查找 |
| ResolvableDependency （非Spring容器管理的对象） | N               | N            | N          | 依赖注入           |



## Spring Bean

### BeanDefinition 元信息

| 属性                     | 说明                                          |
| ------------------------ | --------------------------------------------- |
| Class                    | Bean 全类名，必须是具体类，不能用抽象类或接口 |
| Name                     | Bean 的名称或者ID                             |
| Scope                    | Bean 的作用域（如：singleton，protorype等）   |
| Constructor arguments    | Bean 构造器参数（用于依赖注入）               |
| Properties               | Bean 属性设置（用于依赖注入）                 |
| Autowiring mode          | Bean 自动绑定模式（如：通过名称byName）       |
| Lazy Initialization mode | Bean 延迟初始化模式（延迟和非延迟）           |
| Initialization method    | Bean 初始化回调方法                           |
| Destruction method       | Bean 销毁回调方法                             |



### BeanDefinition 构建

- **通过 BeanDefinitionBuilder**
- **通过 AbstractBeanDefinition及其派生类**

```java
@Configuration
public class BeanDefinitionCreationDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanDefinitionCreationDemo.class);
        // 1.通过 org.springframework.beans.factory.support.BeanDefinitionBuilder 构建
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(User.class);
        // 设置属性
        beanDefinitionBuilder.addPropertyValue("id", 1000L);
        beanDefinitionBuilder.addPropertyValue("username", "ryh");
        // 获取BeanDefinition实例  并不是bean的最终tai态 还是可以修改的
        BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        beanDefinition.setDescription("user test");

        // 2.通过 org.springframework.beans.factory.support.AbstractBeanDefinition及其派生类 构建
        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(SuperUser.class);
        MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();
        // 属性设置
        mutablePropertyValues.addPropertyValue("address", "xj");
        genericBeanDefinition.setPrimary(true);
        genericBeanDefinition.setPropertyValues(mutablePropertyValues);

        applicationContext.registerBeanDefinition("user", beanDefinition);
        applicationContext.registerBeanDefinition("superUser", genericBeanDefinition);
        System.out.println("user:" +  applicationContext.getBean("user") + "   superUser:" + applicationContext.getBean("superUser"));
    }
}
```



### Bean的命名

**在没有指定Bean的名称的时候 Spring会自己生成：**

- **通过 org.springframework.beans.factory.support.DefaultBeanNameGenerator#generateBeanName**
- **通过 org.springframework.context.annotation.AnnotationBeanNameGenerator#generateBeanName**



### Bean的注入

- **通过 xml的方式注入 Bean**
- **通过BeanDefinition Api 命名的方式注入 Bean**
- **通过注解的方式注入 Bean**

```java
@Import(AnnotationBeanDefinitionCreationDemo.Config.class)
public class AnnotationBeanDefinitionCreationDemo {

    public static void main(String[] args) {
        // 开启 Spring 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 注册 AnnotationBeanDefinitionCreationDemo  配置类
        applicationContext.register(AnnotationBeanDefinitionCreationDemo.class);

        // 通过 BeanDefinition Api 命名的方式注入 Bean
        registerBean(applicationContext, "user-01");
        // Spring根据自己的规则生成对应的名称  
        registerBean(applicationContext, "");

        // 启动容器
        applicationContext.refresh();

        // 通过注解的方式注入 Bean
        // 1.通过 @Component 注入
        // 2.通过 @Bean 注入
        // 3.通过 @Import 注入
        System.out.println("当前 spring 容器中存在Config类型的bean信息:" + applicationContext.getBeansOfType(Config.class));
        System.out.println("当前 spring 容器中存在User类型的bean信息:" + applicationContext.getBeansOfType(User.class));

        // 关闭容器
        applicationContext.close();
    }

    public static void registerBean(BeanDefinitionRegistry registry, String beanName) {
        BeanDefinitionBuilder beanDefinitionBuilder = genericBeanDefinition(User.class);
        beanDefinitionBuilder.addPropertyValue("id", 2L).addPropertyValue("username", "ex");
        if (StringUtils.hasText(beanName)) {
            // 显示创建 beanName 并注册 beanDefinition
            registry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        } else {
            // 未命名的 bean 注册方式
            BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinitionBuilder.getBeanDefinition(), registry);
        }
    }

    @Component
    public static class Config {
        @Bean(name = {"user", "user-02"})
        public User user() {
            return new User(1L, "ryh");
        }
    }
}
```



### Bean的实例化

- **构造方法**
- **静态工厂方法**
- **工厂方法**
- **FactoryBean**
- **ServiceLoader（jdk和Spring的）**
- **AutowireCapableBeanFactory**



### Bean的初始化

- **@PostConstruct**
- **InitializingBean#afterPropertiesSet()**
- **通过Api,注解,AbstractBeanDefinition指定initMethod**

**执行顺序：@PostConstruct > InitializingBean#afterPropertiesSet() > initMethod**

**非延迟初始化在Spring 应用上下文完成后，被初始化。**

**延迟初始化是按需初始化，在需要的时候，被初始化。**



### Bean的作用域

| 来源        | 说明                                                     |
| ----------- | -------------------------------------------------------- |
| singleTon   | 默认的Spring Bean作用域，一个BeanFactory有且仅有一个实例 |
| prototype   | 原型作用域，每次依赖查找和依赖注入生成新Bean对象         |
| request     | 将 Spring Bean存储在 ServletRequest 中                   |
| session     | 将 Spring Bean存储在 HttpSession 中                      |
| application | 将 Spring Bean存储在 ServletContext 中                   |



**Spring容器没有办法管理prototype Bean的完整生命周期，也没有办法记录实例的存在。销毁回调方法将不会执行们可以利用BeanPostProcessor进行清扫工作。**

1.  **结论一：**
    **SingleTon Bean 无论依赖查找还是依赖注入 均为同一个的对象**
    **Prototype Bean 无论依赖查找还是依赖注入 均为新生成的对象**
2.  **结论二：**
    **如果依赖注入集合类型的对象，SingleTon Bean 和 Prototype Bean 均会存在一个**
    **Prototype Bean 有别于其它地方依赖注入进来的 bean**
3. **结论三：**
    **无论是 SingleTon Bean 还是 Prototype Bean 都会执行初始化回调函数** 
    **但是只有 SingleTon Bean 会执行 销毁方法回调**



### Bean的生命周期

#### 注解 BeanDefinition

```java
public class AnnotatedBeanDefinitionParsingDemo {

    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 基于 java 注解 的 AnnotatedBeanDefinitionReader
        AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(beanFactory);
        int beanDefinitionCountBefore = beanFactory.getBeanDefinitionCount();
        // 注册当前类 （非 @Component 标注的类）
        reader.register(AnnotatedBeanDefinitionParsingDemo.class);
        int beanDefinitionCountAfter = beanFactory.getBeanDefinitionCount();
        System.out.println("加载的BeanDefinition数量:" + (beanDefinitionCountAfter - beanDefinitionCountBefore));
        // 普通的 Class 作为 Component 注册到 Spring Ioc 容器中 通常 Bean 名称 通常是由 BeanNameGenerator 生成的
        // 注解实现 AnnotatedBeanNameGenerator 由此生成
        System.out.println(beanFactory.getBean("annotatedBeanDefinitionParsingDemo", AnnotatedBeanDefinitionParsingDemo.class));
    }
}
```



#### Bean 元数据配置

```java
public class BeanMetDataConfigurationDemo {

    public static void main(String[] args) {
        // DefaultListableBeanFactory implements BeanDefinitionRegistry
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 实例化基于 Properties 资源 BeanDefinitionReader
        PropertiesBeanDefinitionReader reader = new PropertiesBeanDefinitionReader(beanFactory);
        String location = "/user.properties";
        // 基于 ClassPath 加载 properties 资源
        Resource resource = new ClassPathResource(location);
        // 指定字符集编码
        EncodedResource encodedResource = new EncodedResource(resource, "UTF-8");
        int beanDefinitionsCount = reader.loadBeanDefinitions(encodedResource);
        System.out.println("读取到的BeanDefinition数量:" + beanDefinitionsCount);
        // 依赖查找
        System.out.println(beanFactory.getBean("user", User.class));
    }
}
```



#### BeanDefinition的合并过程

```java
public class MergedBeanDefinitionDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(MergedBeanDefinitionDemo.class);

        // 注册 BeanDefinition -> GenericBeanDefinition
        registerUserBeanDefinition(applicationContext);
        registerSuperUserBeanDefinition(applicationContext);
        registerTopUserBeanDefinition(applicationContext);

        applicationContext.refresh();
        // RootBeanDefinition 不参与合并 BeanDefinition 的操作
        // org.springframework.beans.factory.support.AbstractBeanFactory.getMergedBeanDefinition(java.lang.String, org.springframework.beans.factory.config.BeanDefinition, org.springframework.beans.factory.config.BeanDefinition)
        // 不存在 parentName 直接 new 一个 RootBeanDefinition 返回  并且将此 BeanDefinition 的名称 放入 合并完成后的map mergedBeanDefinitions 中
        // 合并之后 BeanDefinition中的字段 beanClass 从String类型转换为Class类型
        System.out.println("user:" + applicationContext.getBean("user", User.class));

        // 将 GenericBeanDefinition 合并成 RootBeanDefinition 并且覆盖 parent的相关配置 通过追加属性的方式 将子定义中的属性加入进来
        System.out.println("superUser:" + applicationContext.getBean("superUser", User.class));
        System.out.println("topUser:" + applicationContext.getBean("topUser", User.class));

        applicationContext.close();
    }

    private static void registerUserBeanDefinition(BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(User.class);
        createCommonProperty(beanDefinitionBuilder);
        registry.registerBeanDefinition("user", beanDefinitionBuilder.getBeanDefinition());
    }

    private static void registerSuperUserBeanDefinition(BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(SuperUser.class);
        // 设置 父 BeanDefinition 的名称
        beanDefinitionBuilder.setParentName("user");
        beanDefinitionBuilder.addPropertyValue("address", "qh");
        registry.registerBeanDefinition("superUser", beanDefinitionBuilder.getBeanDefinition());
    }

    private static void registerTopUserBeanDefinition(BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(TopUser.class);
        // 设置 父 BeanDefinition 的名称
        beanDefinitionBuilder.setParentName("superUser");
        beanDefinitionBuilder.addPropertyValue("level", 1);
        registry.registerBeanDefinition("topUser", beanDefinitionBuilder.getBeanDefinition());
    }

    private static void createCommonProperty(BeanDefinitionBuilder beanDefinitionBuilder) {
        beanDefinitionBuilder.addPropertyValue("id", 100L);
        beanDefinitionBuilder.addPropertyValue("username", "ryh");
        beanDefinitionBuilder.addPropertyValue("city", City.BEIJING);
        beanDefinitionBuilder.addPropertyValue("resource", null);
        beanDefinitionBuilder.addPropertyValue("workCities", new City[]{City.BEIJING, City.CHENGDU});
    }
}
```



#### Bean实例化

##### Bean实例化前

**org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor**



##### Bean的实例化

**实例化方式**

- **传统实例化方式：实例化策略：InstantiationStrategy**
- **构造器依赖注入(通常是按照类型来进行注入的)**

**org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean**



##### Bean实例化后

**Bean 属性赋值 判断**

**org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor#postProcessAfterInstantiation**



#### Bean 属性赋值前

**Bean 属性值元信息**

- **PropertyValues**

**Bean 属性赋值前回调**

- **org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor#postProcessProperties   Spring5.1 后**
- **org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor#postProcessPropertyValues  Spring5.1 前**



#### Bean 初始化

**Spring Aware 接口**

- **BeanNameAware**
- **BeanClassLoaderAware** 
- **BeanFactoryAware**
- **EnvironmentAware**
- **EmbeddedValueResolverAware**
- **ResourceLoaderAware**
- **ApplicationEventPublisherAware**
- **MessageSourceAware**
- **ApplicationContextAware**

**普通的BeanFactory只进行前三个 aware 接口逇回调，如果是ApplciationContext则会进行所有接口的 aware 回调**



**Bean Aware 接口回调**

**接口回调执行顺序：BeanNameAware -> BeanClassLoaderAware -> BeanFactoryAware**



**Spring Aware 接口回调**

对于后面的几种接口是通过 `org.springframework.context.support.ApplicationContextAwareProcessor#invokeAwareInterfaces` **执行的**



##### Bean 初始化前

- **Bean 实例化**
- **Bean 属性赋值**
- **Bean Aware 接口回调**
- **方法回调** `org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization`



##### Bean 初始化

1. **@PostConstruct 标注方法** `依赖于注解驱动，org.springframework.context.annotation.CommonAnnotationBeanPostProcessor 继承org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor 进行处理` 
2. **实现 InitializingBean 接口的 afterPropertiesSet() 方法** `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.invokeInitMethods  1`
3. **自定义初始化方法** `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.invokeInitMethods  2`



##### Bean 初始化后

`org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization`



##### Bean 初始化完成



**preInstantiateSingletons 将已经注册的 BeanDefinition 初始化成 SpringBean，可以确保 SpringBean 完全初始化后进行回调**

**如果是 BeanFactory 需要显示的执行  beanFactory.preInstantiateSingletons()，SmartInitializingSingleton 一般会在 SpringApplicationContext 中进行调用**

#### Bean 销毁

1. **@PreDestroy 标注方法** `org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor#postProcessBeforeDestruction`
2. **实现 DisposableBean 接口的 destroy() 方法 **``org.springframework.beans.factory.support.DisposableBeanAdapter#destroy``
3. **自定义销毁方法** `org.springframework.beans.factory.support.DisposableBeanAdapter#destroy`



##### Bean 垃圾回收



#### 面试题

##### **BeanFactoryPostProcessor和BeanPostProcessor的区别：**

**BeanFactoryPostProcessor是Spring BeanFactory（ConfigurableListableBeanFactory）的后置处理器，用于扩展BeanFactory，或通过BeanFactory进行依赖查找和依赖注入**

**BeanFactoryPostProcessor必须有Spring ApplicationContext执行，BeanFactory无法和它直接交互**

**BeanFactoryPostProcessor直接与BeanFactory相关联，属于N:1的关系**



##### BeanFactory 怎样处理 Bean 生命周期：

- **BeanDefinition 注册阶段 org.springframework.beans.factory.support.DefaultListableBeanFactory#registerBeanDefinition**
- **BeanDefinition 合并阶段 org.springframework.beans.factory.support.AbstractBeanFactory#getMergedBeanDefinition**
- **Bean 实例化前阶段 org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation**
- **Bean 实例化阶段 org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBeanInstance**
- **Bean 实例化后阶段 org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#populateBean**
- **Bean 属性赋值前阶段 org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#populateBean**
- **Bean 属性赋值阶段 org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#populateBean**
- **Bean 属性赋值后阶段 org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#populateBean**
- **Bean 初始化前阶段 org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean**
- **Bean 初始化阶段 org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean**
- **Bean 初始化后阶段 org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean**
- **Bean 初始化完成阶段 org.springframework.beans.factory.config.ConfigurableListableBeanFactory#preInstantiateSingletons**
- **Bean 销毁前阶段 org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#destroyBean**
- **Bean 销毁阶段 org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#destroyBean**

