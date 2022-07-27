package com.ydles.system.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

/**
 * 黑名单ip拦截
 */
@Component
public class IpFilter implements Ordered, GlobalFilter {
    // 业务逻辑 黑名单ip拦截
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.拿到请求和响应
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 2.通过请求拿到远程地址,再拿到主机名
        InetSocketAddress remoteAddress = request.getRemoteAddress();

        String hostString = remoteAddress.getHostString();
        String hostName = remoteAddress.getHostName();
        System.out.println("hostName = " + hostName);
        System.out.println("hostString = " + hostString);

        // 拿到具体黑名单,直接返回，拒绝访问
        if (hostString.equals("192.168.1.10")){
            // 设置响应码(403,禁止访问)
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        // 过滤器链继续往后走(放行)
        return chain.filter(exchange);
    }

    // 指定过滤器顺序,越小越先执行
    @Override
    public int getOrder() {
        return 1;
    }
}
