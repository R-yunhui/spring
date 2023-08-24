# Spring Cloud Gateway

## 简介

![gateway图](http://ren-bed.oss-cn-beijing.aliyuncs.com/img/gateway图.png)

Gateway是在Spring生态系统之上构建的API网关服务，基于Spring 5，Spring Boot 2和 Project Reactor等技术。Gateway旨在提供一种简单而有效的方式来对API进行路由，以及提供一些强大的过滤器功能， 例如：熔断、限流、重试等。

Spring Cloud Gateway 具有如下特性：

- 基于Spring Framework 5, Project Reactor 和 Spring Boot 2.0 进行构建。
- 动态路由：能够匹配任何请求属性。
- 可以对路由指定 Predicate（断言）和 Filter（过滤器）。
- 集成Hystrix的断路器功能。
- 集成 Spring Cloud 服务发现功能。
- 易于编写的 Predicate（断言）和 Filter（过滤器）。
- 请求限流功能。
- 支持路径重写。

## 相关概念

- Route（路由）：路由是构建网关的基本模块，它由ID，目标URI，一系列的断言和过滤器组成，如果断言为true则匹配该路由。
- Predicate（断言）：指的是Java 8 的 Function Predicate。 输入类型是Spring框架中的ServerWebExchange。 这使开发人员可以匹配HTTP请求中的所有内容，例如请求头或请求参数。如果请求与断言相匹配，则进行路由。
- Filter（过滤器）：指的是Spring框架中GatewayFilter的实例，使用过滤器，可以在请求被路由前后对请求进行修改。

**请求流程图：**

![gateway工作原理](http://ren-bed.oss-cn-beijing.aliyuncs.com/img/gateway工作原理.png)

![请求过程](http://ren-bed.oss-cn-beijing.aliyuncs.com/img/请求过程.png)

- org.springframework.web.reactive.DispatcherHandler#handle  
- org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping#getHandlerInternal  断言匹配获取Route
- org.springframework.web.reactive.result.SimpleHandlerAdapter#handle
- org.springframework.cloud.gateway.handler.FilteringWebHandler#handle 组装DefaultGatewayFilterChain
- org.springframework.cloud.gateway.handler.FilteringWebHandler.DefaultGatewayFilterChain#filter  执行所有GateWayFilter

## Route（路由）

**核心配置类：org.springframework.cloud.gateway.config.GatewayAutoConfiguration  初始化所需的Bean **   

### RouteDefinitionLocator

![路由定位器UML图](http://ren-bed.oss-cn-beijing.aliyuncs.com/img/路由定位器UML图.png)

- org.springframework.cloud.gateway.route.CachingRouteDefinitionLocator  -->  缓存目标RouteDefinitionLocator 为routeDefinitions提供缓存功能
- org.springframework.cloud.gateway.route.CompositeRouteDefinitionLocator  -->  组合多种 RouteDefinitionLocator 的实现，为 routeDefinitions提供统一入口

- org.springframework.cloud.gateway.config.PropertiesRouteDefinitionLocator   -->  从配置文件加载初始化路由
- org.springframework.cloud.gateway.route.RouteDefinitionRepository  --> 从存储器中加载初始化
- org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator  -->  从注册中心加载初始化

**org.springframework.cloud.gateway.route.CachingRouteDefinitionLocator    缓存路由定义定位器**

```java
// RouteDefinitionLocator 包装实现类，实现了路由定义的本地缓存功能 并且监听 RefreshRoutesEvent 事件 动态刷新路由定义定位器的缓存
// org.springframework.cloud.gateway.route.RouteRefreshListener
public class CachingRouteDefinitionLocator
		implements RouteDefinitionLocator, ApplicationListener<RefreshRoutesEvent> {

	private static final String CACHE_KEY = "routeDefs";

    // 实际使用的路由定义定位器
	private final RouteDefinitionLocator delegate;

	private final Flux<RouteDefinition> routeDefinitions;

    // 路由定义的本地缓存
	private final Map<String, List> cache = new ConcurrentHashMap<>();

	public CachingRouteDefinitionLocator(RouteDefinitionLocator delegate) {
		this.delegate = delegate;
		routeDefinitions = CacheFlux.lookup(cache, CACHE_KEY, RouteDefinition.class)
				.onCacheMissResume(this::fetch);
	}
    
    // ......

    /**
     * 监听 RefreshRoutesEvent 事件 动态刷新路由的缓存
     */
	@Override
	public void onApplicationEvent(RefreshRoutesEvent event) {
		fetch().materialize().collect(Collectors.toList())
				.doOnNext(routes -> cache.put(CACHE_KEY, routes)).subscribe();
	}
}
```

**org.springframework.cloud.gateway.route.CompositeRouteDefinitionLocator  路由定义定位器合并提供统一的getRouteDefinitions方法入口**

```java
/**
 * @author Spencer Gibb
 */
public class CompositeRouteDefinitionLocator implements RouteDefinitionLocator {

	private static final Log log = LogFactory
			.getLog(CompositeRouteDefinitionLocator.class);

	/**
     * 所有路由定义定位器实例集合
     */
	private final Flux<RouteDefinitionLocator> delegates;

	private final IdGenerator idGenerator;

	public CompositeRouteDefinitionLocator(Flux<RouteDefinitionLocator> delegates) {
		this(delegates, new AlternativeJdkIdGenerator());
	}

	public CompositeRouteDefinitionLocator(Flux<RouteDefinitionLocator> delegates,
			IdGenerator idGenerator) {
		this.delegates = delegates;
		this.idGenerator = idGenerator;
	}

	@Override
	public Flux<RouteDefinition> getRouteDefinitions() {
        // 将各个RouteDefinitionLocator的getRouteDefinitions合并返回统一的Flux<RouteDefinition>
		return this.delegates.flatMap(RouteDefinitionLocator::getRouteDefinitions)
				.flatMap(routeDefinition -> Mono.justOrEmpty(routeDefinition.getId())
						.defaultIfEmpty(idGenerator.generateId().toString())
						.publishOn(Schedulers.elastic()).map(id -> {
							if (routeDefinition.getId() == null) {
								routeDefinition.setId(id);
								if (log.isDebugEnabled()) {
									log.debug("Id set on route definition: "
											+ routeDefinition);
								}
							}
							return routeDefinition;
						}));
	}
}
```

### RouteLocator

![CompositeRouteLocator](http://ren-bed.oss-cn-beijing.aliyuncs.com/img/CompositeRouteLocator.png)

- org.springframework.cloud.gateway.route.RouteDefinitionRouteLocator    基于路由定义的定位器
- org.springframework.cloud.gateway.route.CachingRouteLocator   基于缓存的路由定位器
- org.springframework.cloud.gateway.route.CompositeRouteLocator   基于组合方式的路由定位器

**org.springframework.cloud.gateway.route.CachingRouteLocator#getRoutes()**

```java
/**
 * @author Spencer Gibb
 * 基于缓存的路由定位器
 */
public class CachingRouteLocator
		implements Ordered, RouteLocator, ApplicationListener<RefreshRoutesEvent> {

	private static final String CACHE_KEY = "routes";

    /** 实际使用的路由定位器 */
	private final RouteLocator delegate;

	private final Flux<Route> routes;

    /** 缓存路由的map */
	private final Map<String, List> cache = new ConcurrentHashMap<>();

	public CachingRouteLocator(RouteLocator delegate) {
		this.delegate = delegate;
		routes = CacheFlux.lookup(cache, CACHE_KEY, Route.class)
				.onCacheMissResume(this::fetch);
	}

	private Flux<Route> fetch() {
		return this.delegate.getRoutes().sort(AnnotationAwareOrderComparator.INSTANCE);
	}
    
    // ................

	@Override
	public Flux<Route> getRoutes() {
		return this.routes;
	}

    /**
     * 通过监听 RefreshRoutesEvent 事件 动态刷新路由的缓存
     */
	@Override
	public void onApplicationEvent(RefreshRoutesEvent event) {
		fetch().materialize().collect(Collectors.toList())
				.doOnNext(routes -> cache.put(CACHE_KEY, routes)).subscribe();
	}
    
    //  ...................

}
```

最终使用的是CachingRouteLocator，它包装了CompositeRouteLocator，而CompositeRouteLocator则组合了RouteDefinitionRouteLocator。

### RouteDefinition 转换成 Route 的流程

**org.springframework.cloud.gateway.route.RouteDefinitionRouteLocator**

![路由定义转换为路由的流程](http://ren-bed.oss-cn-beijing.aliyuncs.com/img/路由定义转换为路由的流程.png)

```java
	/**
	 * 对外提供的获取路由的方法
	 */
	@Override
	public Flux<Route> getRoutes() {
        // routeDefinitionLocator => org.springframework.cloud.gateway.route.CompositeRouteDefinitionLocator
		return this.routeDefinitionLocator.getRouteDefinitions().map(this::convertToRoute)
				// TODO: error handling
				.map(route -> {
					if (logger.isDebugEnabled()) {
						logger.debug("RouteDefinition matched: " + route.getId());
					}
					return route;
				});
	}

	/**
	 * 将路由定义转换为路由
	 */
	private Route convertToRoute(RouteDefinition routeDefinition) {
        // combinePredicates主要是对找出来的predicate进行and操作
		AsyncPredicate<ServerWebExchange> predicate = combinePredicates(routeDefinition);
        
        // getFilters 主要是利用loadGatewayFilters获取filter，使用AnnotationAwareOrderComparator进行排序
		// loadGatewayFilters利用工厂方法，使用GatewayFilterFactory根据config 获取具体的GatewayFilter实例
		List<GatewayFilter> gatewayFilters = getFilters(routeDefinition);

		return Route.async(routeDefinition).asyncPredicate(predicate)
				.replaceFilters(gatewayFilters).build();
	}

	private AsyncPredicate<ServerWebExchange> combinePredicates(
			RouteDefinition routeDefinition) {
		List<PredicateDefinition> predicates = routeDefinition.getPredicates();
		AsyncPredicate<ServerWebExchange> predicate = lookup(routeDefinition,
				predicates.get(0));

		for (PredicateDefinition andPredicate : predicates.subList(1,
				predicates.size())) {
			AsyncPredicate<ServerWebExchange> found = lookup(routeDefinition,
					andPredicate);
			predicate = predicate.and(found);
		}

		return predicate;
	}

	private List<GatewayFilter> getFilters(RouteDefinition routeDefinition) {
		List<GatewayFilter> filters = new ArrayList<>();

		// TODO: support option to apply defaults after route specific filters?
		if (!this.gatewayProperties.getDefaultFilters().isEmpty()) {
			filters.addAll(loadGatewayFilters(DEFAULT_FILTERS,
					this.gatewayProperties.getDefaultFilters()));
		}

		if (!routeDefinition.getFilters().isEmpty()) {
			filters.addAll(loadGatewayFilters(routeDefinition.getId(),
					routeDefinition.getFilters()));
		}

		AnnotationAwareOrderComparator.sort(filters);
		return filters;
	}

	List<GatewayFilter> loadGatewayFilters(String id,
			List<FilterDefinition> filterDefinitions) {
		// ................
	}
```

## Predicate（断言）

![路由谓词工厂UML](http://ren-bed.oss-cn-beijing.aliyuncs.com/img/路由谓词工厂UML.png)

路由谓词配置工厂由一整套谓词来进行配置转发的不同情况。

![路由谓词工厂按功能划分](http://ren-bed.oss-cn-beijing.aliyuncs.com/img/路由谓词工厂按功能划分.png)

| 谓词工厂                        | 备注                                                         |
| :------------------------------ | :----------------------------------------------------------- |
| AfterRoutePredicateFactory      | 此谓词匹配当前日期时间之后发生的请求。                       |
| BeforeRoutePredicateFactory     | 此谓词匹配在当前日期时间之前发生的请求。                     |
| BetweenRoutePredicateFactory    | 此谓词匹配datetime1之后和datetime2之前发生的请求。 datetime2参数必须在datetime1之后。 |
| CookieRoutePredicateFactory     | Cookie Route Predicate Factory有两个参数，cookie名称和正则表达式。此谓词匹配具有给定名称且值与正则表达式匹配的cookie。 |
| HeaderRoutePredicateFactory     | Header Route Predicate Factory有两个参数，标题名称和正则表达式。与具有给定名称且值与正则表达式匹配的标头匹配。 |
| HostRoutePredicateFactory       | Host Route Predicate Factory采用一个参数：主机名模式。该模式是一种Ant样式模式“.”作为分隔符。此谓词匹配与模式匹配的Host标头。 |
| MethodRoutePredicateFactory     | Method Route Predicate Factory采用一个参数：要匹配的HTTP方法。 |
| PathRoutePredicateFactory       | 匹配请求的path。                                             |
| QueryRoutePredicateFactory      | Query Route Predicate Factory有两个参数：一个必需的参数和一个可选的正则表达式。 |
| RemoteAddrRoutePredicateFactory | RemoteAddr Route Predicate Factory采用CIDR符号（IPv4或IPv6）字符串的列表（最小值为1），例如， 192.168.0.1/16（其中192.168.0.1是IP地址，16是子网掩码）。 |
| WeightRoutePredicateFactory     | 使用权重来路由相应请求。                                     |

### RoutePredicateFactory

Spring Cloud Gateway将路由匹配作为Spring WebFlux HandlerMapping基础架构的一部分。 Spring Cloud Gateway包括许多内置的Route Predicate工厂。 所有这些Predicate都与HTTP请求的不同属性匹配。 多个Route Predicate工厂可以进行相互的组合。

#### AfterRoutePredicateFactory

在指定时间之后的请求可以匹配该路由

```json
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true   # 开启从注册中心动态创建路由的功能,利用微服务名称进行路由
      routes:
        - id: gateway_after_route   # 路由的ID
          uri: lb://springcloud-user-8081/   # 路由到的微服务地址
          predicates: # 断言配置 在指定时间之后的请求可以匹配该路由
            - After=2021-05-10T10:30:00+08:00[Asia/Shanghai]
```

#### BeforeRoutePredicateFactory

在指定时间之前的请求可以匹配该路由

```json
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true   # 开启从注册中心动态创建路由的功能,利用微服务名称进行路由
      routes:
        - id: gateway_before_route
          uri: lb://springcloud-user-8081/   # 路由到的微服务地址
          predicates: # 断言配置 在指定时间之前的请求可以匹配该路由
            - Before=2021-05-10T10:30:00+08:00[Asia/Shanghai]
```

#### BetweenRoutePredicateFactory

在指定时间之间的请求可以匹配该路由

```json
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true   # 开启从注册中心动态创建路由的功能,利用微服务名称进行路由
      routes:
        - id: gateway_between_route
          uri: lb://springcloud-user-8081/   # 路由到的微服务地址
          predicates: # 断言配置 在指定时间之间的请求可以匹配该路由
            - Between=2021-05-10T11:30:00+08:00[Asia/Shanghai], 2021-05-10T12:30:00+08:00[Asia/Shanghai]
```

#### CookieRoutePredicateFactory

带有指定的Cookie的请求会匹配该路由

```json
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true   # 开启从注册中心动态创建路由的功能,利用微服务名称进行路由
      routes:
        - id: gateway_cookie_route
          uri: lb://springcloud-user-8081/   # 路由到的微服务地址
          predicates: # 断言配置 带有指定Cookie的请求会匹配该路由
            - Cookie=username, mike
```

#### HeaderRoutePredicateFactory

带有指定请求头的请求会匹配该路由（支持正则）

```json
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true   # 开启从注册中心动态创建路由的功能,利用微服务名称进行路由
      routes:
        - id: gateway_header_route
          uri: lb://springcloud-user-8081/   # 路由到的微服务地址
          predicates: # 断言配置 带有指定请求头的请求会匹配该路由（支持正则）
            - Header=X-Request-Id, \w+
```

#### HostRoutePredicateFactory

带有指定Host的请求会匹配该路由

```json
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true   # 开启从注册中心动态创建路由的功能,利用微服务名称进行路由
      routes:
        - id: gateway_host_route
          uri: lb://springcloud-user-8081/   # 路由到的微服务地址
          predicates: # 断言配置 带有指定Host的请求会匹配该路由
            - Host=**.macrozheng.com
```

#### MethodRoutePredicateFactory

发送指定的方法的请求会匹配该路由

```json
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true   # 开启从注册中心动态创建路由的功能,利用微服务名称进行路由
      routes:
        - id: method_route
          uri: lb://springcloud-user-8081/   # 路由到的微服务地址
          predicates: # 断言配置 发送指定的方法的请求会匹配该路由
```

#### PathRoutePredicateFactory

发送指定路径的请求会匹配该路由

```json
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true   # 开启从注册中心动态创建路由的功能,利用微服务名称进行路由
      routes:
        - id: gateway_path_route
          uri: lb://springcloud-user-8081/   # 路由到的微服务地址
          predicates: # 断言配置 发送指定路径的请求会匹配该路由
            - Path=/pathRoutePredicateFactory/{id}
```

#### QueryRoutePredicateFactory

带指定查询参数的请求可以匹配该路由

```json
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true   # 开启从注册中心动态创建路由的功能,利用微服务名称进行路由
      routes:
        - id: gateway_query_route
          uri: lb://springcloud-user-8081/   # 路由到的微服务地址
          predicates: # 断言配置 带指定查询参数的请求可以匹配该路由
            - Query=username
```

#### RemoteAddrRoutePredicateFactory

发送指定方式的方法的请求会匹配该路由

```json
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true   # 开启从注册中心动态创建路由的功能,利用微服务名称进行路由
      routes:
        - id: gateway_remoteAddr_route
          uri: lb://springcloud-user-8081/   # 路由到的微服务地址
          predicates: # 断言配置 从指定远程地址发起的请求可以匹配该路由
            - RemoteAddr=192.168.1.1/24
```

#### WeightRoutePredicateFactory

使用权重来路由相应请求，以下表示有80%的请求会被路由到springcloud-user-8081，20%会被路由到springcloud-user-8082

```json
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true   # 开启从注册中心动态创建路由的功能,利用微服务名称进行路由
      routes:
		- id: gateway_weight_high
          uri: lb://springcloud-user-8081/   # 路由到的微服务地址
          predicates: # 断言配置 使用权重来路由相应请求，以下表示有80%的请求会被路由到springcloud-user-8081，20%会被路由到springcloud-user-8082
            - Weight=group1, 8
        - id: gateway_weight_low
          uri: lb://springcloud-user-8082/   # 路由到的微服务地址
          predicates:
            - Weight=group1, 2		
          
```

#### **自定义谓词工厂**

**继承org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory 抽象类**

```java
package com.ral.admin.springcloud.handler.pridicate;

import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.http.HttpCookie;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.function.Predicate;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-10 11:10
 * @Describe: VIP用户路由谓词工厂
 * @Modify:
 */
public class VipUserRoutePredicateFactory extends AbstractRoutePredicateFactory<VipUserRoutePredicateFactory.Config> {

    public VipUserRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return (GatewayPredicate) serverWebExchange -> {
            List<HttpCookie> cookies = serverWebExchange.getRequest().getCookies().get(config.getVipTag());
            boolean isVip = false;
            if (!CollectionUtils.isEmpty(cookies)) {
                // TODO 判断cookies中的参数信息是否符合VIP用户
                isVip = true;
            }
            return isVip;
        };
    }

    public static class Config {

        /**
         * VIP用户标识
         */
        private String vipTag = "vipTag";

        public String getVipTag() {
            return vipTag;
        }

        public void setVipTag(String vipTag) {
            this.vipTag = vipTag;
        }
    }
}

```

配置文件：

```json
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true   # 开启从注册中心动态创建路由的功能,利用微服务名称进行路由
      routes:
        - id: vipUser_route
          uri: lb://springcloud-user-8081/   # 路由到的微服务地址
          predicates:
			# 从cookie中获取指定的信息
            - VipUser=vipTag, vip
```

## Filter（过滤器）

Spring-Cloud-Gateway的过滤器接口分为两种：

- GlobalFilter : 全局过滤器，不需要在配置文件中配置，作用在所有的路由上，最终通过GatewayFilterAdapter包装成GatewayFilterChain可识别的过滤器

- GatewayFilter : 需要通过spring.cloud.routes.filters 配置在具体路由下，只作用在当前路由上或通过spring.cloud.default-filters配置在全局，作用在所有路由上


**核心类：org.springframework.cloud.gateway.handler.FilteringWebHandler**

- loadFilters方法是将全局路由使用GatewayFilterAdapter包装成GatewayFilter
- handle方法
  - 获取当前请求使用的路由Route
  - 获取路由配置的过滤器集合route.getFilters()
  - 合并全过滤器与路由配置过滤器combined
  - 对过滤器排序AnnotationAwareOrderComparator.sort
  - 通过过滤器集合构建顶级链表DefaultGatewayFilterChain，并对其当前请求调用链表的filter方法。

```java
/**
 * 包装加载全局的过滤器，将全局过滤器(GlobalFilter)包装成GatewayFilter
 */	
private static List<GatewayFilter> loadFilters(List<GlobalFilter> filters) {
	return filters.stream().map(filter -> {
        // 将所有的全局过滤器包装成网关过滤器 org.springframework.core.Ordered
		GatewayFilterAdapter gatewayFilter = new GatewayFilterAdapter(filter);
        // 判断全局过滤器是否实现了可排序接口
		if (filter instanceof Ordered) {
			int order = ((Ordered) filter).getOrder();
            // 包装成可排序的网关过滤器
			return new OrderedGatewayFilter(gatewayFilter, order);
		}
		return gatewayFilter;
	}).collect(Collectors.toList());
}

@Override
public Mono<Void> handle(ServerWebExchange exchange) {
   // RoutePredicateHandlerMapping#getHandlerInternal的时候放入的Route实例
	Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
    // 获取Route实例上的GatewayFilter
	List<GatewayFilter> gatewayFilters = route.getFilters();
    
    // 组合全局的过滤器与路由配置的过滤器
	List<GatewayFilter> combined = new ArrayList<>(this.globalFilters);
    
    // 添加路由配置过滤器到集合尾部
	combined.addAll(gatewayFilters);
    
    // 对过滤器进行排序
	// TODO: needed or cached?
	AnnotationAwareOrderComparator.sort(combined);

	if (logger.isDebugEnabled()) {
		logger.debug("Sorted gatewayFilterFactories: " + combined);
	}

    // 创建过滤器链表对其进行链式调用
	return new DefaultGatewayFilterChain(combined).filter(exchange);
}

/**
 * 全局过滤器的包装类，将全局路由包装成统一的网关过滤器
 */
private static class GatewayFilterAdapter implements GatewayFilter {

    /** 全局过滤器的包装类，将全局路由包装成统一的网关过滤器 */
	private final GlobalFilter delegate;

	GatewayFilterAdapter(GlobalFilter delegate) {
		this.delegate = delegate;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		return this.delegate.filter(exchange, chain);
	}
}

```

### GlobalFilter

```java
/**
 * @Author: RenYunHui
 * @Date: 2021-05-09 14:57
 * @Describe: 全局过滤器，作用在所有的路由上，最终通过GatewayFilterAdapter包装成GatewayFilterChain可识别的过滤器
 * @Modify:
 */
@Component
@Slf4j
public class TestFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("执行TestFilter");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

```

### GatewayFilterFactory

路由过滤器可用于修改进入的HTTP请求和返回的HTTP响应，路由过滤器只能指定路由进行使用。Spring Cloud Gateway 内置了多种路由过滤器，他们都由GatewayFilter的工厂类来产生。

#### AddRequestParameterGatewayFilterFactory

- 给请求添加参数的过滤器。

```json
spring:
  application:
    name: springcloud-gateway-server-8090
  cloud:
    gateway:
      routes:
        - id: after_route   # 路由的ID
          uri: lb://springcloud-user-8081/   # 路由到的微服务地址
          predicates: # 断言配置 在指定时间之后的请求可以匹配该路由
            - After=2021-05-10T11:30:00+08:00[Asia/Shanghai]
          filters: # 过滤器配置 给请求添加参数的过滤器。
            - AddRequestParameter=username, mike
```

```
发送：curl http://127.0.0.1:8090/addRequestHeaderGatewayFilterFactory
等价于：curl http://127.0.0.1:8081/addRequestHeaderGatewayFilterFactory?username=mike
```

#### StripPrefixGatewayFilterFactory

- 对指定数量的路径前缀进行去除的过滤器。

```json
spring:
  application:
    name: springcloud-gateway-server-8090
  cloud:
    gateway:
      routes:
        - id: after_route   # 路由的ID
          uri: lb://springcloud-user-8081/   # 路由到的微服务地址
          predicates: # 断言配置 在指定时间之后的请求可以匹配该路由
            - After=2021-05-10T11:30:00+08:00[Asia/Shanghai]
          filters: # 过滤器配置 对指定数量的路径前缀进行去除的过滤器。
            - StripPrefix=2
```

```
发送：curl http://127.0.0.1:8090/test/aaa/stripPrefixGatewayFilterFactory
等价于：curl http://127.0.0.1:8081/stripPrefixGatewayFilterFactory
```

#### PrefixPathGatewayFilterFactory

- 对原有路径进行增加操作的过滤器。

```json
spring:
  application:
    name: springcloud-gateway-server-8090
  cloud:
    gateway:
      routes:
        - id: after_route   # 路由的ID
          uri: lb://springcloud-user-8081/   # 路由到的微服务地址
          predicates: # 断言配置 在指定时间之后的请求可以匹配该路由
            - After=2021-05-10T11:30:00+08:00[Asia/Shanghai]
          filters: # 过滤器配置 对指定数量的路径前缀进行去除的过滤器。
            - PrefixPath=/test
```

```
发送：curl http://127.0.0.1:8090/prefixPathGatewayFilterFactory
等价于：curl http://127.0.0.1:8081/test/prefixPathGatewayFilterFactory
```

#### AddRequestHeaderGatewayFilterFactory

- 为原始请求添加请求头的过滤器。

```json
spring:
  application:
    name: springcloud-gateway-server-8090
  cloud:
    gateway:
      routes:
        - id: after_route   # 路由的ID
          uri: lb://springcloud-user-8081/   # 路由到的微服务地址
          predicates: # 断言配置 在指定时间之后的请求可以匹配该路由
            - After=2021-05-10T11:30:00+08:00[Asia/Shanghai]
          filters: # 过滤器配置 为原始请求添加请求头的过滤器。
            - AddRequestHeader=username, mike
```

```
发送：curl http://127.0.0.1:8090/addRequestHeaderGatewayFilterFactory
等价于：curl http://127.0.0.1:8081/addRequestHeaderGatewayFilterFactory -H "username:mike" 
```

#### RedirectToGatewayFilterFactory

- 重定向到https://www.baidu.com/ 且携带一个Location=https://www.baidu.com/ 的Header的过滤器。

```json
spring:
  application:
    name: springcloud-gateway-server-8090
  cloud:
    gateway:
      routes:
        - id: after_route   # 路由的ID
          uri: lb://springcloud-user-8081/   # 路由到的微服务地址
          predicates: # 断言配置 在指定时间之后的请求可以匹配该路由
            - After=2021-05-10T11:30:00+08:00[Asia/Shanghai]
          filters: # 重定向到https://www.baidu.com/ 且携带一个Location=https://www.baidu.com/ 的Header
            - RedirectTo=302, https://www.baidu.com/
```

#### RequestRateLimiterGatewayFilterFactory

- 限流过滤器（基于Redis和RequestRateLimiter）

```json
spring:
  application:
    name: springcloud-gateway-server-8090
  cloud:
    gateway:
      routes:
        - id: after_route   # 路由的ID
          uri: lb://springcloud-user-8081/   # 路由到的微服务地址
          predicates: # 断言配置 在指定时间之后的请求可以匹配该路由
            - After=2021-05-10T11:30:00+08:00[Asia/Shanghai]
		  filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 1  # 每秒允许处理的请求数量（令牌桶每秒填充平均速率）
                redis-rate-limiter.burstCapacity: 2  # 每秒最大处理的请求数量（令牌桶总容量）
                key-resolver: "#{@PathKeyResolver}"  # 限流策略，对应策略的Bean（用于限流的键的解析器的 Bean 对象的名字。它使用 SpEL 表达式根据#{@beanName}从 Spring 容器中获取 Bean 对象）
```

- 基于请求路径去限流

```java
package com.ral.admin.springcloud.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-10 15:06
 * @Describe:
 * @Modify:
 */
public class PathKeyResolver implements KeyResolver {
    
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        // 根据uri去限流
        // 限流的规则辉作用在路径上
        // 访问 http://localhost:8090/requestRateLimiterGatewayFilterFactory
        // 限流规则：redis-rate-limiter.replenishRate: 1  # 每秒允许处理的请求数量（令牌桶每秒填充平均速率）
        //         redis-rate-limiter.burstCapacity: 2  # 每秒最大处理的请求数量（令牌桶总容量）
        return Mono.just(exchange.getRequest().getURI().getPath());
    }
}

// -------------
    @Bean(name = "PathKeyResolver")
    public PathKeyResolver pathKeyResolver() {
        return new PathKeyResolver();
    }
```

当多次请求则会出现状态码为429的错误

#### RetryGatewayFilterFactory

- 当调用返回500时会进行重试（默认3次）

```json
spring:
  application:
    name: springcloud-gateway-server-8090
  cloud:
    gateway:
      routes:
        - id: after_route   # 路由的ID
          uri: lb://springcloud-user-8081/   # 路由到的微服务地址
          predicates: # 断言配置 在指定时间之后的请求可以匹配该路由
            - After=2021-05-10T11:30:00+08:00[Asia/Shanghai]
          filters:
            - name: Retry
              args:
                retries: 3  # 重试次数
                statuses: BAD_GATEWAY  # 返回哪个状态码需要进行重试，返回状态码为5XX进行重试
```

#### 自定义过滤器工厂

**继承org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory 抽象类**

```java
package com.ral.admin.springcloud.handler.filter;

import com.ral.admin.springcloud.handler.pridicate.VipUserRoutePredicateFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-10 16:29
 * @Describe:
 * @Modify:
 */
public class VipUserGatewayFilterFactory extends 		 AbstractGatewayFilterFactory<VipUserGatewayFilterFactory.Config> {

    private static final String NOT_VIP = "当前登录的用户不是VIP用户，不允许访问";

    public VipUserGatewayFilterFactory() {
        super(VipUserGatewayFilterFactory.Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String username = exchange.getRequest().getQueryParams().getFirst("username");
            // TODO 判断用户是否是VIP用户
            if (null != username && username.equals(config.name)) {
                return chain.filter(exchange);
            } else {
                ServerHttpResponse response = exchange.getResponse();
                byte[] bits = NOT_VIP.getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = response.bufferFactory().wrap(bits);
                // 指定编码，以防乱码
                response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
                return response.writeWith(Mono.just(buffer));
            }
        };
    }

    public static class Config {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
```

配置文件：

```json
spring:
  application:
    name: springcloud-gateway-server-8090
  cloud:
    gateway:
      routes:
        - id: after_route   # 路由的ID
          uri: lb://springcloud-user-8081/   # 路由到的微服务地址
          predicates: # 断言配置 在指定时间之后的请求可以匹配该路由
            - After=2021-05-10T11:30:00+08:00[Asia/Shanghai]
          filters:
            - name: VipUser
              args:
                name: vip
```

## Dynamic Routing（动态路由）

#### **AbstractGatewayControllerEndpoint**

**org.springframework.cloud.gateway.actuate.AbstractGatewayControllerEndpoint#save**

**org.springframework.cloud.gateway.actuate.AbstractGatewayControllerEndpoint#delete**

```json
// 添加路由的请求参数：org.springframework.cloud.gateway.route.RouteDefinition
{
    "filters": [
        {
            "args": {
                "_genkey_0": "username",
                "_genkey_1": "jack"
            },
            "name": "AddRequestParameter"
        }
    ],
    "id": "gateway-server",
    "metadata": {},
    "order": 0,
    "predicates": [
        {
            "args": {
                "_genkey_0": "GET"
            },
            "name": "Method"
        }
    ],
    "uri": "lb://springcloud-user-8081/"
}
```

#### **RouteDefinitionRepository**

```java
package com.ral.admin.springcloud.handler.route;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-10 19:28
 * @Describe: 基于nacos动态刷新路由配置
 * @Modify:
 */
@Slf4j
public class NacosRouteDefinitionRepository implements RouteDefinitionRepository {

    /** nacos中路由配置文件的dataIds */
    private static final String GATE_WAY_DATA_IDS = "gateway-route.json";
    /** nacos中路由配置文件的groupId */
    private static final String GATE_WAY_GROUP_IDS = "DEFAULT_GROUP";

    private final ApplicationEventPublisher publisher;
    private final NacosConfigProperties nacosConfigProperties;

    public NacosRouteDefinitionRepository(ApplicationEventPublisher applicationEventPublisher, NacosConfigProperties nacosConfigProperties) {
        this.nacosConfigProperties = nacosConfigProperties;
        this.publisher = applicationEventPublisher;
        addListener();
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        try {
            // 获取配置文件的数据 转换为路由定义
            String content = nacosConfigProperties.configServiceInstance().getConfig(GATE_WAY_DATA_IDS, GATE_WAY_GROUP_IDS,5000);
            List<RouteDefinition> routeDefinitions = getListByStr(content);
            return Flux.fromIterable(routeDefinitions);
        } catch (NacosException e) {
            log.error("从nacos获取路由定义的配置信息失败", e);
        }
        return Flux.fromIterable(CollUtil.newArrayList());
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return null;
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return null;
    }

    /**
     * 添加Nacos监听
     */
    private void addListener() {
        try {
            nacosConfigProperties.configServiceInstance().addListener(GATE_WAY_DATA_IDS, GATE_WAY_GROUP_IDS, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    publisher.publishEvent(new RefreshRoutesEvent(this));
                }
            });
        } catch (NacosException e) {
            log.error("监听nacos配置文件修改事件失败", e);
        }
    }

    /**
     * 获取路由定义信息
     * @param content nacos配置文件信息
     * @return 路由定义信息集合
     */
    private List<RouteDefinition> getListByStr(String content) {
        if (StringUtils.isNotEmpty(content)) {
            return JSONUtil.toList(content, RouteDefinition.class);
        }
        return new ArrayList<>();
    }
}
```

**nacos中的配置**

```json
// 参数：org.springframework.cloud.gateway.route.RouteDefinition
[
  {
    "id": "after_route",
    "predicates": [{
      "name": "After",
      "args": {
        "datetime": "2021-05-11T09:05:00+08:00[Asia/Shanghai]"
      }
    }],
    "uri": "lb://springcloud-user-8081/",
    "filters": []
  }
]
```

