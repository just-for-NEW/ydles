package com.ydles;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class TestJWT {
    public static void main(String[] args) {
        // 设置 jwt令牌一小时存活时间
        JwtBuilder jwtBuilder = Jwts.builder()
                // 设置令牌id
                .setId("27")
                // 主题
                .setSubject("ydlesshancheng")
                // 签发时间
                .setIssuedAt(new Date())
                // 过期时间
               // .setExpiration(new Date())
                // 自定义信息放进jwt
                .claim("name","lk")
                .claim("age","18")
                // 签名和密钥
                .signWith(SignatureAlgorithm.HS256,"ydles");

        // 生成jwt
        String jwt = jwtBuilder.compact();
        System.out.println(jwt);

        // 解密
        Claims claims = Jwts.parser().setSigningKey("ydles").parseClaimsJws(jwt).getBody();
        System.out.println(claims);
    }
}
