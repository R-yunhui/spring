package com.ral.young.jwt.service;

import com.ral.young.jwt.util.JwtUtil;
import org.springframework.stereotype.Service;

/**
 * @author renyunhui
 * @date 2023-06-14 11:24
 * @since 1.0.0
 */
@Service
public class TestService {

    public String getToken(String id, String name) {
        return JwtUtil.getJwtToken(id, name);
    }

    public String testToken() {
        return "testToken success";
    }
}
