#配置中心地址
> 172.19.1.1:9001
```aidl
//创建bootstrap.yml
spring:
    cloud:
        config:
            profile:环境
            label:版本【分支】
            uri: http://172.19.1.1:9001/
```
#服务发现地址
> 172.19.1.0:9000
```aidl
eureka:
  client:
    service-url:
      defaultZone: http://ip:9000/eureka/
```
#网关地址
> 172.19.1.2:9002
#zookeeper
> 172.19.1.3 
>
> 2181:客户端端口|2888:   |3888: 
```aidl
docker run --name zk --net wzqnet --ip 172.19.1.3 -p 2181:2181 -p 2888:2888 -p 3888:3888 --restart always -d zookeeper
```
#kafka
> 172.19.1.4:9092
> 
> https://hub.docker.com/r/wurstmeister/kafka/
```aidl
docker run --name kafka --net wzqnet --ip 172.19.1.4 -p 9092:9092 -e KAFKA_ADVERTISED_HOST_NAME=kafka01 -e KAFKA_CREATE_TOPICS="test:1:1" -e KAFKA_ZOOKEEPER_CONNECT=172.19.1.3:2181 -d  wurstmeister/kafka
//进入kafka容器
docker exec -it kafka /bin/bash
//创建主题
/opt/kafka/bin/kafka-topics.sh --create --zookeeper 172.19.1.3:2181 --replication-factor 1 --partitions 1 --topic my-test
//查看创建的主题
/opt/kafka/bin/kafka-topics.sh --list --zookeeper 172.19.1.3:2181
//发送消息
/opt/kafka/bin/kafka-console-producer.sh --broker-list  172.19.1.4:9092 --topic my-test
//读取消息
/opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server 172.19.1.4:9092 --topic my-test --from-beginning
```
#kafka-manager
> 172.19.1.5:9000
> https://hub.docker.com/r/sheepkiller/kafka-manager/
```sbtshell
docker run -it --restart always -d --name=kafka-manager --net wzqnet --ip 172.19.1.5 -p 8080:9000 -e ZK_HOSTS="172.19.1.3:2181" sheepkiller/kafka-manager
