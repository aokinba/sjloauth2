FROM registry.cn-hangzhou.aliyuncs.com/springcloud-cn/java:8u172-jre-alpine
ARG JAR_FILE
ENV PROFILE default
ADD target/sjloauth2-client-1.0-SNAPSHOT.jar /opt/app.jar
EXPOSE 8080
ENTRYPOINT java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -Duser.timezone=Asia/Shanghai -Dfile.encoding=UTF-8 -Dspring.profiles.active=${PROFILE} -jar /opt/app.jar