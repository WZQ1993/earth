###docker搭建zk
1. 下载zk镜像
```aidl
docker pull zookeeper
```
2.启动
```aidl
docker run -d --net wzqnet --ip 172.19.1.0 -p 9000:9000 -v c:/dev/spring-cloud-demo/eureka:/data/server --name eureka registry.cn-hangzhou.aliyuncs.com/wzq/application
```