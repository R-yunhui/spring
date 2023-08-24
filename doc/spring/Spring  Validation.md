# Spring  Validation

## 一、基于方法级别的数据校验

### 校验（普通校验 + 分组校验）

**针对`service`层进行参数校验**

```java
@Data
public class UserInfoBo {
 
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
    * 分组校验（指定特定的分组 groups）
    */
    @NotBlank(message = "密码不能为空", groups = SelectService.class)
    private String password;

    @NotNull(message = "性别不能为空", groups = SaveService.class)
    private Byte gender;

    @NotBlank(message = "邮箱不能为空", groups = SaveService.class)
    @Email(message = "邮箱格式不能为空", groups = SaveService.class)
    private String email;

    private String phone;
    
    /**
    * @Valid 做级联校验 校验内部对象中的待验证的属性
    */
    @Valid
    private List<Book> bookList;
    
    public static class Book {
        @NotBlank(message = "书名不能为空")
        private String bookName;
    }
}
```

**在定义的接口上可以添加  `@Validated`**

* @Validated：用在类（接口）、方法和方法参数上。但不能用于成员属性（field）、支持分组
* @Valid：可以用在方法、构造函数、方法参数和成员属性（field）上、不支持分组、支持级联校验

```java
@Validated
public interface IUserService {

    @Validated(value = SaveService.class)
    void testOne(@Valid UserInfoBo userInfo);

    @Validated(value = SelectService.class)
    void testTwo(@Valid UserInfoBo userInfo);

    void testThree(@NotBlank(message = "用户名不能为空") String username);
}
```

### 遇到的问题

**【注1】：**

**在校验方法入参的约束时，若是`@Override`父类/接口的方法，那么这个入参约束只能写在父类/接口上面，否则会跑出下述异常：**

**覆盖另一个方法的方法一定不能重新定义参数约束的配置**

![image-20210520111932186](http://ren-bed.oss-cn-beijing.aliyuncs.com/img/image-20210520111932186.png)

**除非现类写的约束和接口定义的约束一模一样，也是可以正常校验**

**【注2】：**

**分组校验如果不仅需要此分组的特定校验也需要默认校验则需要让分组的接口继承：`javax.validation.groups.Default`**

```java
public interface SaveService extends Default {
}
```

**【注3】：**

**循环依赖问题**：**如果A,B存在循环依赖，并且A上存在@Validated注解，那么在B中注入A的时候，需要加上@Lazy帮助解决多次引用A不是同一个版本的问题**

![image-20210524110644630](http://ren-bed.oss-cn-beijing.aliyuncs.com/img/image-20210524110644630.png)

**推荐使用`@Lazy`的注解方式解决：**

```java
@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    @Autowired
    @Lazy
    private IBookService bookService;

    @Override
    public void testOne(UserInfoBo userInfo) {
        log.info("测试一：" + JSONUtil.toJsonStr(userInfo));
    }
}
```

## 二、基于MVC的数据校验

### **针对controller层的参数校验**(包含普通校验和分组校验，略过)

**controller层代码**

```java
@RestController
@RequestMapping(value = "app")
public class AppIndexController {

    @Autowired
    private AppIndexService appIndexService;

    @PostMapping(value = "/searchApps")
    @ApiOperation(value = "查询我个人的应用", notes = "根据应用名称查询我的空间应用列表")
    @ResponseBody
    public Mono<Result<PageResult<List<DcAppIndexVO>>>> searchApps(@Validated @RequestBody PageParam<AppIndexParam> pageParam) {
        return Mono.just(Result.buildSuccessResult(appIndexService.searchApps(pageParam)));
    }
}
```

**校验对象**

```java
@Data
public class PageParam<T extends BaseDto> {

    @NotNull
    @Min(1)
    private Integer page = 1;

    @NotNull
    @Min(1)
    private Integer size = 20;

    @Valid
    private T data;

    public Integer getOffset() {
        return (page - 1) * size;
    }

}
```

**明显可以发现，区别于像Service层基于方法级别的校验，controller层如果需要对传入的参数进行校验，直接在方法的参数上加上@Validated/@Valid注解即可，其原因是MVC框架在内部引入了spring validation，隐示的在调用方法前对方法的入参对象进行了校验，万变不离其中，底层依旧使用的是Hibernate-validation包提供的功能。下面剖析下具体是在MVC生命周期的哪一步实现的，怎么实现的**。

**DispatcherServlet通过HandlerMapping匹配url路径定位到具体处理这个url的controller方法，上述调用得到是一个HandlerExecutionChain，HandlerExecutionChain类结构如下：**

![1622084742122](http://ren-bed.oss-cn-beijing.aliyuncs.com/img/1622084742122.png)

**在拦截器执行完毕之后，handler就会去调用真正的方法，实际上它是一个HandlerAdaptor，以RequestMappingHandlerAdapter为例，在调用方法之前，会调用解析器对Request的数据进行解析，转换成想要的数据类型，然后对参数对象进行校验，以@RequestBody标注的参数为例，解析工具类中执行的过程如下**

```java
public class RequestResponseBodyMethodProcessor extends AbstractMessageConverterMethodProcessor {
    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {

        parameter = parameter.nestedIfOptional();
        //将请求数据封装到DTO对象中
        Object arg = readWithMessageConverters(webRequest, parameter, parameter.getNestedGenericParameterType());
        String name = Conventions.getVariableNameForParameter(parameter);

        if (binderFactory != null) {
            WebDataBinder binder = binderFactory.createBinder(webRequest, arg, name);
            if (arg != null) {
                // 执行数据校验
                validateIfApplicable(binder, parameter);
                if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
                    throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
                }
            }
            if (mavContainer != null) {
                mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + name, binder.getBindingResult());
            }
        }
        return adaptArgumentIfNecessary(arg, parameter);
    }
}
```

```java
public abstract class AbstractMessageConverterMethodArgumentResolver implements HandlerMethodArgumentResolver {
protected void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
  Annotation[] annotations = parameter.getParameterAnnotations();
  for (Annotation ann : annotations) {
   Validated validatedAnn = AnnotationUtils.getAnnotation(ann, Validated.class);
   if (validatedAnn != null || ann.annotationType().getSimpleName().startsWith("Valid")) {
    Object hints = (validatedAnn != null ? validatedAnn.value() : AnnotationUtils.getValue(ann));
    Object[] validationHints = (hints instanceof Object[] ? (Object[]) hints : new Object[] {hints});
    binder.validate(validationHints);
    break;
   }
  }
 }
}
```

**由此可见，具体校验的逻辑存在于参数解析器的抽象类中，对于所有继承了它的解析器均生效。并且由代码可以看到@Validated、@Valid注解都做了识别。以上便是在方法参数上加校验注解就可以自动校验的大体流程。**