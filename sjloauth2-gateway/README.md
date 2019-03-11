## gateway-oauth

##### 演示，运行eureka、auth、gateway、client

- 访问http://192.168.56.1:8080/client/test
因为没有权限，跳转到登录页面
  ![1551924636812](https://github.com/aokinba/sjloauth2/blob/master/doc/img-folder/QQ%E6%88%AA%E5%9B%BE20190307101720.png)
-  在路由配置文件加上自定义过滤器 MyPre *sjloauth2-gateway*
``` yml
         routes:
           - id: CLIENT-A           #网关路由到订单服务order-service
             uri: lb://CLIENT-A
             predicates:
               - Path=/client/**
             filters:
               - MyPre=     #必须要放在StripPrefix前面，不要path路径会变
               - StripPrefix=1
```
- 权限过滤器实现，判断没有token则跳转回调地址。*sjloauth2-gateway/MyPreGatewayFilterFactory*  (关键代码)
``` java
            //如果请求没有带token，则跳转到授权页面。授权成功会跳转到回调地址
            if (StringUtils.isBlank(token)
                    && !path.contains("uaa/oauth/authorize")
                    && StringUtils.isBlank(code)
                    && StringUtils.isBlank(TokenContextHolder.getToken())) {
                //当请求不携带Token或者token为空时，直接设置请求状态码为401，返回
                exchange.getResponse().getHeaders().add("Location", "/uaa/oauth/authorize?client_id=test_server&"
                        + "response_type=code&redirect_uri=http://192.168.56.1:8080/authlogin&state=" + path);
                exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                return exchange.getResponse().setComplete();
            }
```
- 授权成功后跳转到回调地址http://192.168.56.1:8080/authlogin?code=XXXXXX&state=请求目录 *sjloauth2-gateway/LoginController* 
``` java
@Controller
public class LoginController {

    @GetMapping("/authlogin")
    public String index(Model model) {
        model.addAttribute("userAttributes", "123");
        return "authlogin";
    }
}
```
- 返回authlogic页面，该页面会根据回调的code和state，向服务器请求和存放token  *sjloauth2-gateway\src\main\resources\templates\authlogin.html*
- 权限过滤器再将服务器存放的token转到下游的headers *sjloauth2-gateway/MyPreGatewayFilterFactory*  (关键代码)
``` java
            //如果已授权，则将token带入下游的header里
            if (StringUtils.isNotBlank(TokenContextHolder.getToken())) {
                ServerWebExchange ch = exchange.mutate()
                        .request(r -> r.headers(headers -> headers.setBearerAuth(TokenContextHolder.getToken())))
                        .build();
                return chain.filter(ch);
            }
```
