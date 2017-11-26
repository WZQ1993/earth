#配置中心地址
```aidl
//创建bootstrap.yml
spring:
    cloud:
        config:
            profile:环境
            label:版本【分支】
            uri: http://104.225.234.158:6001/
```
#服务发现地址
```aidl
eureka:
  client:
    service-url:
      defaultZone: http://104.225.234.158:9002[9001]/eureka/
```