package com.ral.young.jwt.filter;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.ral.young.jwt.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * token 过滤器
 *
 * @author renyunhui
 * @date 2023-06-14 13:12
 * @since 1.0.0
 */
@Slf4j
@Component
public class TokenFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 判断请求头中是否包含token信息并且是否合法
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("token");
        if (CharSequenceUtil.isBlank(token)) {
            token = httpServletRequest.getHeader("authorization");
        }
        boolean success = JwtUtil.checkToken(token);
        if (!success) {
            log.info("过滤器过滤掉 token 不合法的请求");
            return;
        }
        chain.doFilter(request, response);
    }
}
