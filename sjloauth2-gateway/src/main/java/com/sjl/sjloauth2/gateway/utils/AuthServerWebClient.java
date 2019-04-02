/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sjl.sjloauth2.gateway.utils;

import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerExchangeFilterFunction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthServerWebClient {

    @Autowired
    private LoadBalancerExchangeFilterFunction lbFunction;

    @Autowired
    private RestTemplate restTemplate;

    public Mono<String> getToken(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", "test_server");
        formData.add("grant_type", "authorization_code");
        String ipAddress = System.getenv("IP_ADDRESS");
        ipAddress = StringUtils.isBlank(ipAddress) ? "127.0.0.1" : ipAddress;
        formData.add("redirect_uri", "http://" + ipAddress + ":8080/authlogin");
        formData.add("code", code);

        return WebClient
                .builder().baseUrl("http://auth-server")
                .filter(lbFunction)
                .build()
                .post()
                .uri("/uaa/oauth/token")
                .header(HttpHeaders.AUTHORIZATION, "Basic dGVzdF9zZXJ2ZXI6c2VjcmV0")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(Exception.class, err -> {
                    throw new RuntimeException(err.getMessage());
                })
                .onErrorResume(Exception.class, err -> {
                    throw new RuntimeException(err.getMessage());
                });

    }
}
