package com.ydles.system.filters;

import com.ydles.system.util.JwtUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 鉴权过滤器 验证token
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    private static final String AUTHORIZE_TOKEN = "token";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求和响应体
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();
        if (path.contains("admin/login")){
            // admin 属于登录操作 放行
            return chain.filter(exchange);
        }

        // 访问资源
        // 什么状态是登录 header token=jwt
        HttpHeaders headers = request.getHeaders();
        String jwt = headers.getFirst(AUTHORIZE_TOKEN);
        // 没有jwt 拒绝
        if (StringUtils.isEmpty(jwt)){
            // 401(未认证)
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // 有jwt  校验
        try {
            JwtUtil.parseJWT(jwt);
        }catch (Exception e){
         // logger.warn 错误的jwt 如黑客攻击
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }


        // 通过登录验证 放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
