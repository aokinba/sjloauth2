package com.sjl.sjloauth2.gateway;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthFeightClient {

    @LoadBalanced
    @Bean
    RestTemplate loadBalanced() {
        return new RestTemplate();
    }

    public Mono<String> doOtherStuff() {
        return WebClient.builder().baseUrl("http://stores")
                .build()
                .get()
                .uri("/stores")
                .retrieve()
                .bodyToMono(String.class);
    }
}
