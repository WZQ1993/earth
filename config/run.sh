#!/bin/bash
# screen -r config 连接会话
gradle bootJar
java -jar config-1.0.jar --spring.profiles.active=prod >>app.log &

