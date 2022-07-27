package com.ydles.system.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class UrlFilter implements Ordered, GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 危险日志打印 如:/admin/delete 记录
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        URI uri = request.getURI();
        String path = uri.getPath();
        System.out.println("path = " + path);

        if (path.contains("admin/delete")){
            // logger.warn()
            System.out.println("几点几分哪个ip删除管理员");
        }
        // 放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
