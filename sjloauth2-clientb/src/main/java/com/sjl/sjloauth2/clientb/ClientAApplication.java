package com.sjl.sjloauth2.clientb;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableDiscoveryClient
//@EnableResourceServer
@RestController
@EnableHystrix
//public class ClientAApplication extends ResourceServerConfigurerAdapter {
public class ClientAApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientAApplication.class, args);
    }

    @RequestMapping("/test")
    public String test(HttpServletRequest request) {
        //通过getHeaderNames获得所有头名字的Enumeration集合

        Enumeration<String> headNames = request.getHeaderNames();
        while (headNames.hasMoreElements()) {
            String headName = headNames.nextElement();
            System.out.println(headName + ":" + request.getHeader(headName));
        }

        System.out.println("getQueryString : " + request.getPathInfo());

        return "test22222222";
    }

    @RequestMapping("/userinfo")
    public Mono<Map> userinfo(HttpServletRequest request) {
        System.out.println("=====================userinfo=================");
        Map map = new HashMap();
        map.put("id", "9527");
        map.put("login", "苏敬龙");
        map.put("name", "苏敬龙");
        map.put("sub", "苏敬龙");
        map.put("avatar_url", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1550666170043&di=67d097870ccd1317c61d217330c64d20&imgtype=0&src=http%3A%2F%2Fimage.uc.cn%2Fo%2Fwemedia%2Fs%2Fupload%2F2018%2F8d2e520e1c7caea3791ca037db7b323ax640x427x29.jpeg%3B%2C4%2Cjpegx%3B3%2C700x.jpg");
        map.put("avatar", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1550666170043&di=67d097870ccd1317c61d217330c64d20&imgtype=0&src=http%3A%2F%2Fimage.uc.cn%2Fo%2Fwemedia%2Fs%2Fupload%2F2018%2F8d2e520e1c7caea3791ca037db7b323ax640x427x29.jpeg%3B%2C4%2Cjpegx%3B3%2C700x.jpg");
        map.put("roles", new String[]{"read", "write"});

        return Mono.just(map);
    }

//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable()
//                .authorizeRequests()
//                .antMatchers(HttpMethod.POST, "/actuator/refresh").permitAll() //允许git配置文件刷新                
//                .antMatchers("/actuator/hystrix.stream").permitAll()//允许断容器仪表访问                
//                .antMatchers(HttpMethod.GET, "/test").hasAuthority("WRIGTH_WRITE")
//                .antMatchers("/**").authenticated();
//    }
//    @Override
//    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
//        resources
//                .resourceId("WRIGTH")
//                .tokenStore(jwtTokenStore());
//    }
//
//    @Bean
//    protected JwtAccessTokenConverter jwtTokenConverter() {
//        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//        converter.setSigningKey("springcloud123");
//        return converter;
//    }
//
//    @Bean
//    public TokenStore jwtTokenStore() {
//        return new JwtTokenStore(jwtTokenConverter());
//    }
}
