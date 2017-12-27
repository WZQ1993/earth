#!/bin/bash
# screen -r eureka 连接会话
# gradle bootJar
# 启动命令
# docker run -d --net wzqnet --ip 172.19.1.0 -p 9000:9000 -v c:/dev/spring-cloud-demo/eureka:/data/server --name eureka registry.cn-hangzhou.aliyuncs.com/wzq/application
java -jar /data/server/build/libs/eureka-1.0.jar --spring.profiles.active=prod              >> /data/server/app.log