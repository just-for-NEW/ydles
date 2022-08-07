package com.ydles.webGateway.filter;

import com.ydles.webGateway.utils.UrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter implements Ordered, GatewayFilter {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

            // 1.1登陆时：放行
        String path = request.getURI().getPath();
        System.out.println("path = " + path);
        // path.contains("/oauth/login") || path.contains("/oauth/toLogin")
        if (!UrlUtil.hasAuthorize(path)){
            // 放行
            return chain.filter(exchange);
        }


            // 1.2用户访问时：
            // 1判断当前请求是否为登录请求，是的话，则放行
            // 2  判断cookie中是否存在信息, 没有的话，拒绝访问
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        HttpCookie cookie = cookies.getFirst("uid");
        if (cookie == null){
            // 未认证(401)
             response.setStatusCode(HttpStatus.UNAUTHORIZED);
             return response.setComplete();
        }

        // 3 判断redis中令牌是否存在，没有的话，拒绝访问
        String jti = cookie.getValue();
        String jwt = stringRedisTemplate.opsForValue().get(jti);
        if (StringUtils.isEmpty(jwt)){
            // 拒绝访问
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // 拼接jwt到请求头中
        request.mutate().header("Authorization","Bearer " + jwt);

        // 放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
