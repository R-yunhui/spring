package com.ral.young.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

/**
 * @author renyunhui
 * @description 这是一个PlusEncoderInterceptor类
 * @date 2024-08-27 14-18-31
 * @since 1.0.0
 */
public class PlusEncoderInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        return execution.execute(new HttpRequestWrapper(request) {
            @Override
            public URI getURI() {
                URI u = super.getURI();
                String strictlyEscapedQuery = StringUtils.replace(u.getRawQuery(), "+", "%2B");
                return UriComponentsBuilder.fromUri(u).replaceQuery(strictlyEscapedQuery).build(true).toUri();
            }
        }, body);
    }
}
