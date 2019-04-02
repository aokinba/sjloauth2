## docker-compose
- 在子目录下添加Dockerfil文件
```
FROM registry.cn-hangzhou.aliyuncs.com/springcloud-cn/java:8u172-jre-alpine
ARG JAR_FILE
ENV PROFILE default
ADD target/sjloauth2-auth-1.0-SNAPSHOT.jar /opt/app.jar
EXPOSE 8080
ENTRYPOINT java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -Duser.timezone=Asia/Shanghai -Dfile.encoding=UTF-8 -Dspring.profiles.active=${PROFILE} -jar /opt/app.jar
```

- 在父目录添加docker-compose.yml文件
```
version: '3' # 表示该 Docker-Compose 文件使用的是 Version 2 file
services:
  eureka1:  # 指定服务名称
    restart: always
    build: ./sjloauth2-eureka  # 指定 Dockerfile 所在路径
    ports:    # 指定端口映射
      - "8761:8761"
    environment:
      - EUREKA_SERVER_HOST=192.168.1.180
      - EUREKA_SERVER_PORT=8761
      - IP_ADDRESS=192.168.1.180
  auth:  # 指定服务名称
    restart: always
    build: ./sjloauth2-auth  # 指定 Dockerfile 所在路径
    ports:    # 指定端口映射
      - "7777:7777"
    environment:
      - EUREKA_SERVER_HOST=192.168.1.180
      - EUREKA_SERVER_PORT=8761
      - IP_ADDRESS=192.168.1.180
```

- cmd进入docker-compose所在目录
- docker-compose up     //命令启动所有项目
- docker-compose down --rmi all   //关闭所有项目并删除所有镜像