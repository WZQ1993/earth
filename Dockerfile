# 构建命令
# docker build -t="registry.cn-hangzhou.aliyuncs.com/wzq/application" .
FROM java
LABEL author="wangziqing"
LABEL des="application应用容器"
LABEL version="1.0"
# EXPOSE 9000 映射容器的指定端口到随机宿主端口
# VOLUME ["f:/docker/eureka"] 随机挂载目录，感觉不太实用
# CMD ["/bin/bash","/data/server/run.sh"]
