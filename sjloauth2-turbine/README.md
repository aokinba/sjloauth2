# Turbine 聚合 Hystrix
## Turbine服务器
- 添加配置文件：开放hystrix.stream端口，和设置要监控的服务
``` yml
management:
  security:
    enabled: false
  endpoints:
    web:
      exposure:
        include: hystrix.stream
turbine:
  appConfig: client-a
  clusterNameExpression: "'default'"
```
- 启动文件
``` java
@EnableTurbine
@EnableHystrixDashboard
public class TurbineApplication {

    public static void main(String[] args) {
        SpringApplication.run(TurbineApplication.class, args);
    }
}
```


## client客户端
- 添加配置文件：开放hystrix.stream端口，和设置要监控的服务
``` yml
management:
  security:
    enabled: false
  endpoints:
    web:
      exposure:
        include: hystrix.stream
turbine:
  appConfig: client-a
  clusterNameExpression: "'default'"
```
- 启动文件添加@EnableHystrix
- antMatchers("/actuator/hystrix.stream").permitAll()//允许断容器仪表访问  
```
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/actuator/refresh").permitAll() //允许git配置文件刷新
                .antMatchers("/actuator/hystrix.stream").permitAll()//允许断容器仪表访问                
                .antMatchers(HttpMethod.GET, "/test").hasAuthority("WRIGTH_WRITE")
                .antMatchers("/**").authenticated();
    }
```
- 测试controller
```
@RestController
public class TestController {
    
    @GetMapping("/hystrixTest")
    @HystrixCommand(fallbackMethod = "defaultUser")
    public String hystrixTest(@RequestParam String username) throws Exception {
        if (username.equals("spring")) {
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
```


## 运行效果

