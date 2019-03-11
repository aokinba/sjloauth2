package com.sjl.sjloauth2.client.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class TestController {

    @Autowired
    private LoadBalancerExchangeFilterFunction lbFunction;
    
    private static final Logger log = LoggerFactory.getLogger(TestController.class);
    
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/hystrixTest")
    @HystrixCommand(fallbackMethod = "defaultUser", commandKey = "hystrixTest")
    public String hystrixTest(@RequestParam String username) throws Exception {
        if (username.equals("spring")) {
            return "This is real user";
        }
        if (username.equals("test")) {
            Thread.sleep(5000);
            return "This is real user";
        } else {
            throw new Exception();
        }
    }

    @GetMapping("/test2")
    public Mono<String> test2() throws Exception {
        WebClient client = WebClient.builder().baseUrl("http://client-b").filter(lbFunction).build();
        return client.post()
                .uri("/test")
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(Exception.class, err -> {
                    throw new RuntimeException(err.getMessage());
                });
    }
    
    @GetMapping("/test3")
    public String helloByRestTemplate(String name){
        log.info("client sent. RestTemplate方式, 参数: {}",name);

        String url = "http://localhost:7072/test";
        String result = restTemplate.getForObject(url,String.class);

        log.info("client received. RestTemplate方式, 结果: {}",result);
        return result;
    }

    /**
     * 出错则调用该方法返回友好错误
     *
     * @param username
     * @return
     */
    public String defaultUser(String username) {
        return "The user does not exist in this system";
    }
}
