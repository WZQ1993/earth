#!/bin/bash
# screen -r wzq 连接会话
gradle bootJar
java -jar eureka-1.0.jar --spring.profiles.active=prod1 >>app1.log &

