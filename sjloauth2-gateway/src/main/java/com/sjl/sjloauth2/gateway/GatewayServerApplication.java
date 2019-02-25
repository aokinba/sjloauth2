package com.sjl.sjloauth2.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@Controller
public class GatewayServerApplication {

    @Autowired
    @LoadBalanced
    private RestTemplate loadBalanced;

    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }
//
//    @Bean
//    public RouteLocator testRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("add_request_parameter_route", r
//                        -> r.path("/client/test").filters(f -> f.addRequestParameter("access_tokens", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NTExMTkwNTIsInVzZXJfbmFtZSI6ImFkbWluIiwiYXV0aG9yaXRpZXMiOlsiV1JJR1RIX1dSSVRFIiwiV1JJR1RIX1JFQUQiXSwianRpIjoiYzJlZmE2MWUtNTFjNy00NDJlLWJjNGEtNTQzMmY2OWY5ZmQwIiwiY2xpZW50X2lkIjoidGVzdF9zZXJ2ZXIiLCJzY29wZSI6WyJXUklHVEgiLCJyZWFkIl19.AGyFjPpuHrjZ_n_3XdIoBBs_IQRV2kuDpJvYxHj-x34"))
//                        .uri("http://localhost:7070/test"))
//                .build();
//    }

    @GetMapping("/")
    public String index() {
        return loadBalanced.getForObject("http://www.baidu.com", String.class);
    }
}
