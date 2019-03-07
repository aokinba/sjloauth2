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
  appConfig: gateway
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


## gateway作hystrix客户端
- 添加配置文件：开放hystrix.stream端口和添加Hystrix过滤器
``` yml
spring:
  application:
    name: gateway
  thymeleaf:
    cache: false
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
      routes:
        - id: CLIENT-A           #网关路由到订单服务order-service
          uri: lb://CLIENT-A
          predicates:
            - Path=/client/**
          filters:
            - MyPre=     #必须要放在StripPrefix前面，不要path路径会变
            - StripPrefix=1
            - name: Hystrix # Hystrix Filter的名称
              args: # Hystrix配置参数
                name: fallbackcmd #HystrixCommand的名字
                fallbackUri: forward:/fallback #fallback对应的uri
                
#Hystrix的fallbackcmd的时间                
hystrix.command.fallbackcmd.execution.isolation.thread.timeoutInMilliseconds: 6000
management:
  security:
    enabled: false
  endpoints:
    web:
      exposure:
        include: hystrix.stream
```
- 启动文件添加@EnableHystrix

- client项目添加测试controller
```
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
```


## 运行效果
- 浏览器运行http://localhost:9088/hystrix
- 输入http://localhost:9088/turbine.stream
- 点击Monitor Stream
![1551924636812](https://github.com/aokinba/sjloauth2/blob/master/img-folder/QQ%E6%88%AA%E5%9B%BE20190307160241.png)
- 分别运行http://192.168.56.1:8080/client/hystrixTest?username=spring  和 http://192.168.56.1:8080/client/hystrixTest?username=sjl  的效果
![1551924636812](https://github.com/aokinba/sjloauth2/blob/master/img-folder/QQ%E6%88%AA%E5%9B%BE20190307160632.png)
