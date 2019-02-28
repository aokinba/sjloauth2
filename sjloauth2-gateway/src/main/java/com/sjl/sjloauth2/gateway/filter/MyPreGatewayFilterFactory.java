/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sjl.sjloauth2.gateway.filter;

import com.sjl.sjloauth2.gateway.utils.AuthServerWebClient;
import com.sjl.sjloauth2.gateway.utils.TokenContextHolder;
import io.micrometer.core.instrument.util.StringUtils;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 *
 * @author Administrator
 */
@Component
public class MyPreGatewayFilterFactory extends AbstractGatewayFilterFactory<MyPreGatewayFilterFactory.Config> {

    @Autowired
    private AuthServerWebClient authServerWebClient;

    public MyPreGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String token = exchange.getRequest().getQueryParams().getFirst("access_token");
            String code = exchange.getRequest().getQueryParams().getFirst("code");
            URI uri = exchange.getRequest().getURI();
            String path = uri.getPath();
            System.out.println("=========================");
            System.out.println("=========================path = " + path + "  code = " + code);
            System.out.println("=========================");
            if ((null == token || token.isEmpty())
                    && !path.contains("uaa/oauth/authorize")
                    && StringUtils.isBlank(code)
                    && StringUtils.isBlank(TokenContextHolder.getToken())) {
                //当请求不携带Token或者token为空时，直接设置请求状态码为401，返回
                exchange.getResponse().getHeaders().add("Location", "/uaa/oauth/authorize?client_id=test_server&"
                        + "response_type=code&redirect_uri=http://192.168.56.1:8080/authlogin&state=" + path);
                exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                return exchange.getResponse().setComplete();
            }

            if (StringUtils.isNotBlank(TokenContextHolder.getToken())) {
                ServerWebExchange ch = exchange.mutate()
                        .request(r -> r.headers(headers -> headers.setBearerAuth(TokenContextHolder.getToken())))
                        .build();
                return chain.filter(ch);
            }

            return chain.filter(exchange);
        };

    }

    public static class Config {
        //Put the configuration properties for your filter here
    }

}