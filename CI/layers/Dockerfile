# 指定基础镜像，这是分阶段构建的前期阶段
FROM 10.88.12.96/jdk/eclipse-temurin:8u442-b06-jdk-focal-web as builder

ARG JAR_FILE

WORKDIR syncplant

COPY ${JAR_FILE} application.jar

# 通过工具spring-boot-jarmode-layertools从application.jar中提取拆分后的构建结果
RUN java -Djarmode=layertools -jar application.jar extract

# 正式构建镜像
FROM 10.88.12.96/jdk/eclipse-temurin:8u442-b06-jdk-focal-web

WORKDIR syncplant

# 前一阶段从jar中提取除了多个文件，这里分别执行COPY命令复制到镜像空间中，每次COPY都是一个layer
COPY --from=builder syncplant/dependencies/ ./
COPY --from=builder syncplant/spring-boot-loader/ ./
COPY --from=builder syncplant/syncplant-dependencies/ ./
COPY --from=builder syncplant/spring-dependencies/ ./
COPY --from=builder syncplant/snapshot-dependencies/ ./
COPY --from=builder syncplant/application/ ./
COPY --from=builder syncplant/tomcat-dependencies/BOOT-INF/lib/ ./tomcat/

ENV TZ="Asia/Shanghai"

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN echo -e '#!/bin/sh\njava -Dloader.path=./$SERVER_TYPE $JAVA_OPTS org.springframework.boot.loader.PropertiesLauncher' >> start.sh

ENV ENV="dev" NACOS_DISCOVERY_NAMESPACE="" NACOS_CONFIG_NAMESPACE="" NACOS_URL="127.0.0.1:8848" NACOS_USERNAME="nacos" NACOS_PASSWORD="nacos" APP_NAME="syncplant-system-pf" JAVA_OPTS="-server -XX:+UseG1GC -XX:G1HeapRegionSize=8 -XX:MaxGCPauseMillis=500  -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:+UseStringDeduplication -XX:+UseContainerSupport -Xms1g -Xmx2g -XX:MetaspaceSize=128m" SERVER_TYPE="tomcat"

ENTRYPOINT ["sh","start.sh"]