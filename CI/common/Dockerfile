# 基础镜像
FROM openjdk:21-slim

ARG APP_NAME

# 设置工作目录
WORKDIR /bubua12

# 配置时区
ENV TZ=Asia/Shanghai
RUN echo "Asia/Shanghai" > /etc/timezone && dpkg-reconfigure -f noninteractive tzdata

# 设置必要的环境变量
ENV ENV="prod" \
    OTEL_EXPORTER_OTLP_ENDPOINT=http://127.0.0.1:4318 \
    NACOS_DISCOVERY_NAMESPACE="" \
    NACOS_CONFIG_NAMESPACE="" \
    NACOS_URL="127.0.0.1:8848" \
    NACOS_USERNAME="nacos" \
    NACOS_PASSWORD="nacos" \
    APP_NAME=${APP_NAME} \
    JAVA_OPTS="-server -XX:+UseG1GC -XX:G1HeapRegionSize=8 -XX:MaxGCPauseMillis=500 -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:+UseStringDeduplication -XX:+UseContainerSupport -Xms1g -Xmx3g -XX:MetaspaceSize=128m" \
    SERVER_TYPE="tomcat"

COPY target/${APP_NAME}.jar /bubua12/app.jar

ENTRYPOINT ["java", "$JAVA_OPTS", "-jar", "app.jar"]
