#!/bin/bash
# screen -r config 连接会话
# 启动命令
# docker run -d --net wzqnet --ip 172.19.1.1 -p 9001:9001 -v c:/dev/spring-cloud-demo/config:/data/server --name config registry.cn-hangzhou.aliyuncs.com/wzq/application
java -jar /data/server/build/libs/config-1.0.jar --spring.profiles.active=prod >> /data/server/app.log
