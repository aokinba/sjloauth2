package com.sjl.sjloauth2.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 调用鉴权
 */
@Component
public class AuthSignatureFilter implements GatewayFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getQueryParams().getFirst("authToken");
        String path = exchange.getRequest().getPath().toString();
        System.out.println("path = " + path);
        if ((null == token || token.isEmpty()) && !path.contains("uaa/oauth/authorize")) {
            //当请求不携带Token或者token为空时，直接设置请求状态码为401，返回
            exchange.getResponse().getHeaders().add("Location", "/uaa/oauth/authorize?client_id=test_server&response_type=code&redirect_uri=http://www.baidu.com");
            exchange.getResponse().setStatusCode(HttpStatus.FOUND);
            return exchange.getResponse().setComplete();
        }
//        exchange.getRequest().getQueryParams().add("access_token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NTExMTkwNTIsInVzZXJfbmFtZSI6ImFkbWluIiwiYXV0aG9yaXRpZXMiOlsiV1JJR1RIX1dSSVRFIiwiV1JJR1RIX1JFQUQiXSwianRpIjoiYzJlZmE2MWUtNTFjNy00NDJlLWJjNGEtNTQzMmY2OWY5ZmQwIiwiY2xpZW50X2lkIjoidGVzdF9zZXJ2ZXIiLCJzY29wZSI6WyJXUklHVEgiLCJyZWFkIl19.AGyFjPpuHrjZ_n_3XdIoBBs_IQRV2kuDpJvYxHj-x34");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -400;
    }
}
