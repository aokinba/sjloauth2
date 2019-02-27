package com.sjl.sjloauth2.gateway.controller;

import com.sjl.sjloauth2.gateway.utils.AuthServerWebClient;
import com.sjl.sjloauth2.gateway.utils.TokenContextHolder;
import io.micrometer.core.instrument.util.StringUtils;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthServerWebClient authServerWebClient;

    /**
     * 获取code
     *
     * @param code
     * @param state
     * @return
     */
    @RequestMapping("/getToken")
    public Mono<String> getToken(String code, String state) {
        if (StringUtils.isBlank(code)) {
            throw new RuntimeException("code 为空");
        }
        return authServerWebClient.getToken(code);
    }

    /**
     * 提交token
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/putToken", method = {RequestMethod.POST})
    public Mono<String> putToken(@RequestBody Map<String, String> map) {
        String token = (String) map.get("token");
        if (StringUtils.isBlank(token)) {
            throw new RuntimeException("token 为空");
        }
        TokenContextHolder.set(token);
        return Mono.just("succeed");
    }
}
