## gateway-oauth

##### 演示，运行eureka、auth、gateway、client

- 访问http://192.168.56.1:8080/client/test
因为没有权限，跳转到登录页面
  ![1551924636812](https://github.com/aokinba/sjloauth2/blob/master/img-folder/QQ%E6%88%AA%E5%9B%BE20190307101720.png)
- 实现：路径：sjloauth2-gateway 在路由配置文件加上自定义过滤器 MyPre
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
- 实现：路径：sjloauth2-gateway/MyPreGatewayFilterFactory  (关键代码)
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


