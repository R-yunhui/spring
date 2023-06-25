package com.ral.young.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * jwt 工具类
 *
 * @author renyunhui
 * @date 2023-06-14 10:34
 * @since 1.0.0
 */
@Slf4j
public class JwtUtil {

    /**
     * 默认过期时间
     */
    public static final long DEFAULT_EXPIRE = 1000 * 60 * 60 * 24;

    /**
     * 加密密钥
     */
    public static final String APP_SECRET = "bcdedit";

    /**
     * 生成 token 字符串
     *
     * @param id       用户id
     * @param nickname 用户昵称
     * @return 加密的token信息
     */
    public static String getJwtToken(Long id, String nickname) {
        return Jwts.builder()
                // 设置 token 头部分
                .setHeaderParam("typ", "JWT").setHeaderParam("alg", "HS256")
                // 设置 token 的主题 subject，自定义
                .setSubject("jwt-demo")
                // 设置 token 的创建时间
                .setIssuedAt(DateUtil.date())
                // 设置过期时间，于何时过期，默认一天
                .setExpiration(new Date(System.currentTimeMillis() + DEFAULT_EXPIRE))
                // 设置 token 的有效载荷
                .claim("id", id).claim("nickname", nickname)
                // 设置签名，使用的加密算法以及密钥
                .signWith(SignatureAlgorithm.HS256, APP_SECRET).compact();
    }

    /**
     * 判断token是否存在与有效
     *
     * @param jwtToken 加密的 jwt token
     * @return 是否过期
     */
    public static boolean checkToken(String jwtToken) {
        if (CharSequenceUtil.isBlank(jwtToken)) {
            log.warn("传递的 jwtToken 为空");
            return false;
        }

        // 过滤掉特殊情况
        if (jwtToken.startsWith("Bearer")) {
            jwtToken = jwtToken.replace("Bearer", "").trim();
        }

        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
            // 比较过期时间
            int compare = DateUtil.compare(DateUtil.date(), claimsJws.getBody().getExpiration());
            return compare < 0;
        } catch (Exception e) {
            log.error("token解析失败,errorMsg:{}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 根据token获取会员id，根据用户 id 查询数据库获取用户基本信息
     *
     * @return 返回解析后的用户 id
     */
    public static String getMemberIdByJwtToken(String jwtToken) {
        if (CharSequenceUtil.isBlank(jwtToken)) {
            log.warn("传递的 jwtToken 为空");
            return CharSequenceUtil.EMPTY;
        }

        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        return (String) claims.get("id");
    }
}
