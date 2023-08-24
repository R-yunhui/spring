### **OpenFeign的执行流程**

**Spring给标记了`@FeignClients`的bean通过JDK动态代理生成代理对象`feign.ReflectiveFeign#newInstance`**

```java
public class ReflectiveFeign extends Feign {
	@Override
	public <T> T newInstance(Target<T> target) {
		//使用Contract解析接口类上的方法和注解，转换单独MethodHandler处理
		Map<String, MethodHandler> nameToHandler = targetToHandlersByName.apply(target);
		// 使用DK动态代理为接口生成代理对象，实际业务逻辑交给 InvocationHandler 处理，其实就是调用 MethodHandler 
		InvocationHandler handler = factory.create(target, methodToHandler);
		T proxy = (T) Proxy.newProxyInstance(target.type().getClassLoader(), new Class<?>[]{target.type()}, handler);
		return proxy;
	}
}
```

**`feign.SynchronousMethodHandler#invoke`实际用来处理请求逻辑**

```java
  @Override
  public Object invoke(Object[] argv) throws Throwable {
    // 创建获取请求模板
    RequestTemplate template = buildTemplateFromArgs.create(argv);
    // 参数的处理
    Options options = findOptions(argv);
    // 默认的重试器  
    Retryer retryer = this.retryer.clone();
    while (true) {
      try {
          // 处理和解码
        return executeAndDecode(template, options);
      } catch (RetryableException e) {
        try {
          retryer.continueOrPropagate(e);
        } catch (RetryableException th) {
          Throwable cause = th.getCause();
          if (propagationPolicy == UNWRAP && cause != null) {
            throw cause;
          } else {
            throw th;
          }
        }
        if (logLevel != Logger.Level.NONE) {
          logger.logRetry(metadata.configKey(), logLevel);
        }
        continue;
      }
    }
  }
```

**`feign.SynchronousMethodHandler#targetRequest `执行请求拦截器生成最终Request**

```java
  Request targetRequest(RequestTemplate template) {
    // 执行具体的请求拦截器
    for (RequestInterceptor interceptor : requestInterceptors) {
      interceptor.apply(template);
    }
    return target.apply(template);
  }
```

**`feign.SynchronousMethodHandler#executeAndDecode`**

```java
Object executeAndDecode(RequestTemplate template, Options options) throws Throwable {
    // 获取最终的请求
    Request request = targetRequest(template);

    // 打印请求日志
    if (logLevel != Logger.Level.NONE) {
      logger.logRequest(metadata.configKey(), logLevel, request);
    }

    Response response;
    long start = System.nanoTime();
    try {
        // feign.Client 执行最终的request请求
      response = client.execute(request, options);
      // ensure the request is set. TODO: remove in Feign 12
      response = response.toBuilder()
          .request(request)
          .requestTemplate(template)
          .build();
    } catch (IOException e) {
      if (logLevel != Logger.Level.NONE) {
        logger.logIOException(metadata.configKey(), logLevel, e, elapsedTime(start));
      }
      throw errorExecuting(request, e);
    }
    long elapsedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);


    
    if (decoder != null)
      return decoder.decode(response, metadata.returnType());

    CompletableFuture<Object> resultFuture = new CompletableFuture<>();
    asyncResponseHandler.handleResponse(resultFuture, metadata.configKey(), response,
        metadata.returnType(),
        elapsedTime);

    try {
      if (!resultFuture.isDone())
        throw new IllegalStateException("Response handling not done");

      return resultFuture.join();
    } catch (CompletionException e) {
      Throwable cause = e.getCause();
      if (cause != null)
        throw cause;
      throw e;
    }
  }
```

**`org.springframework.cloud.openfeign.loadbalancer.FeignBlockingLoadBalancerClient#execute`负载均衡处理调用**

```java
	@Override
	public Response execute(Request request, Request.Options options) throws IOException {
		final URI originalUri = URI.create(request.url());
		String serviceId = originalUri.getHost();
		Assert.state(serviceId != null,
				"Request URI does not contain a valid hostname: " + originalUri);
		ServiceInstance instance = loadBalancerClient.choose(serviceId);
		if (instance == null) {
			String message = "Load balancer does not contain an instance for the service "
					+ serviceId;
			if (LOG.isWarnEnabled()) {
				LOG.warn(message);
			}
			return Response.builder().request(request)
					.status(HttpStatus.SERVICE_UNAVAILABLE.value())
					.body(message, StandardCharsets.UTF_8).build();
		}
		String reconstructedUrl = loadBalancerClient.reconstructURI(instance, originalUri)
				.toString();
		Request newRequest = Request.create(request.httpMethod(), reconstructedUrl,
				request.headers(), request.body(), request.charset(),
				request.requestTemplate());
		return delegate.execute(newRequest, options);
	}
```



### RestTemplate的执行流程

**`org.springframework.web.client.RestTemplate#doExecute`**

```java
	protected <T> T doExecute(URI url, @Nullable HttpMethod method, @Nullable RequestCallback requestCallback,
			@Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {

		Assert.notNull(url, "URI is required");
		Assert.notNull(method, "HttpMethod is required");
		ClientHttpResponse response = null;
		try {
            // 创建请求
			ClientHttpRequest request = createRequest(url, method);
			if (requestCallback != null) {
				requestCallback.doWithRequest(request);
			}
			response = request.execute();
			handleResponse(url, method, response);
			return (responseExtractor != null ? responseExtractor.extractData(response) : null);
		}
		catch (IOException ex) {
			String resource = url.toString();
			String query = url.getRawQuery();
			resource = (query != null ? resource.substring(0, resource.indexOf('?')) : resource);
			throw new ResourceAccessException("I/O error on " + method.name() +
					" request for \"" + resource + "\": " + ex.getMessage(), ex);
		}
		finally {
			if (response != null) {
				response.close();
			}
		}
	}
```

**`org.springframework.http.client.support.InterceptingHttpAccessor#getRequestFactory`**

```java
	@Override
	public ClientHttpRequestFactory getRequestFactory() {
        // 获取所有的拦截器
		List<ClientHttpRequestInterceptor> interceptors = getInterceptors();
		if (!CollectionUtils.isEmpty(interceptors)) {
			ClientHttpRequestFactory factory = this.interceptingRequestFactory;
			if (factory == null) {
				factory = new InterceptingClientHttpRequestFactory(super.getRequestFactory(), interceptors);
				this.interceptingRequestFactory = factory;
			}
            // 返回包含拦截器的请求工程
			return factory;
		}
		else {
            // 返回默认的请求工厂
			return super.getRequestFactory();
		}
	}
```

**`org.springframework.http.client.SimpleClientHttpRequestFactory#createRequest`**

```java
	@Override
	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        // 实际构建请求的方法
		HttpURLConnection connection = openConnection(uri.toURL(), this.proxy);
		prepareConnection(connection, httpMethod.name());

		if (this.bufferRequestBody) {
			return new SimpleBufferingClientHttpRequest(connection, this.outputStreaming);
		}
		else {
			return new SimpleStreamingClientHttpRequest(connection, this.chunkSize, this.outputStreaming);
		}
	}
```

**`org.springframework.http.client.AbstractClientHttpRequest#execute`**

```java
	@Override
	public final ClientHttpResponse execute() throws IOException {
		assertNotExecuted();
        // 将给定的标头和内容写入HTTP请求的抽象模板方法
		ClientHttpResponse result = executeInternal(this.headers);
		this.executed = true;
		return result;
	}
```

**`org.springframework.http.client.AbstractBufferingClientHttpRequest#executeInternal(org.springframework.http.HttpHeaders)`**

```java
	@Override
	protected ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException {
		byte[] bytes = this.bufferedOutput.toByteArray();
		if (headers.getContentLength() < 0) {
			headers.setContentLength(bytes.length);
		}
        // 将给定的标头和内容写入HTTP请求的抽象模板方法
		ClientHttpResponse result = executeInternal(headers, bytes);
		this.bufferedOutput = new ByteArrayOutputStream(0);
		return result;
	}
```

