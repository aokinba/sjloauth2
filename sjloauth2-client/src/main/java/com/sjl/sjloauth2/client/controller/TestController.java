package com.sjl.sjloauth2.client.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/hystrixTest")
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
