# config

## config-server
- 添加配置文件，配置git配置，rabbitMQ用于更新config客户端配置
``` xml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/aokinba/sjl-config.git
          #username:
          #password:
          search-paths: base-config
  application:
    name: sjl-config-git
    
  ## 配置rabbitMQ 信息
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```
- 添加刷新配置端口
``` xml
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
```
- 允许权限访问
``` java
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
    }
}
```
- 启动文件
``` java
@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class ConfigGitApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigGitApplication.class, args);

    }
}
```


## config-client
- 添加配置文件
``` xml
spring:
    application:
        name: client-a
    cloud:
        config:
            discovery:
              enabled: true
              service-id: SJL-CONFIG-GIT  # 注册中心的服务名
            profile: dev  # 指定配置文件的环境
```
- 添加刷新配置端口
``` xml
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
```
- 允许权限访问  路径*client/ClientAApplication*
``` java
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/actuator/refresh").permitAll() //允许git配置文件刷新
                .antMatchers(HttpMethod.GET, "/test").hasAuthority("WRIGTH_WRITE")
                .antMatchers("/**").authenticated();
    }
```

## post访问配置服务器刷新配置 http://localhost:9090/actuator/bus-refresh
