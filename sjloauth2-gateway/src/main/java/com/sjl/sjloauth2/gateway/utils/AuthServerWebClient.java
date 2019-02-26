/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sjl.sjloauth2.gateway.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerExchangeFilterFunction;
import org.springframework.http.HttpEntity;
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

    public String getAuthToken(String code) {
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", "test_server");
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", "http://192.168.56.1:8080/client/test");
        formData.add("code", code);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        headers.add(HttpHeaders.AUTHORIZATION, "Basic dGVzdF9zZXJ2ZXI6c2VjcmV0");

        HttpEntity<MultiValueMap<String, Object>> r = new HttpEntity<>(formData, headers);

        String data = restTemplate.postForObject("http://auth-server/uaa/oauth/token", r, String.class);
        System.out.println(data);
        return data;
    }

    public Mono<String> getToken(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", "test_server");
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", "http://192.168.56.1:8080/client/test");
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
