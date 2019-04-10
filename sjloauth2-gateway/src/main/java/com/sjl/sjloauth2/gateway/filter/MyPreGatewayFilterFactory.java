/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sjl.sjloauth2.gateway.filter;
import io.micrometer.core.instrument.util.StringUtils;
import java.net.URI;
import javax.annotation.Resource;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 *
 * @author Administrator
 */
@Component
public class MyPreGatewayFilterFactory extends AbstractGatewayFilterFactory<MyPreGatewayFilterFactory.Config> {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

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
            String ipAddress = System.getenv("IP_ADDRESS");
            ipAddress = StringUtils.isBlank(ipAddress)?"127.0.0.1":ipAddress;
            String tokenRedis = this.redisTemplate.opsForValue().get("token");
            //如果请求没有带token，则跳转到授权页面。授权成功会跳转到回调地址
            if (StringUtils.isBlank(token)
                    && !path.contains("uaa/oauth/authorize")
                    && StringUtils.isBlank(code)
                    && StringUtils.isBlank(tokenRedis)) {
                //当请求不携带Token或者token为空时，直接设置请求状态码为401，返回
                exchange.getResponse().getHeaders().add("Location", "/uaa/oauth/authorize?client_id=test_server&"
                        + "response_type=code&redirect_uri=http://"+ ipAddress +":8080/authlogin&state=" + path);
                exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                return exchange.getResponse().setComplete();
            }

            //如果已授权，则将token带入下游的header里
            if (StringUtils.isNotBlank(tokenRedis)) {
                ServerWebExchange ch = exchange.mutate()
                        .request(r -> r.headers(headers -> headers.setBearerAuth(tokenRedis)))
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
