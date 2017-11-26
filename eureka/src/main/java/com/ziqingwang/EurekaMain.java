package com.ziqingwang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
/**
 * eureka服务注册中心
 */
@EnableEurekaServer
@SpringBootApplication
public class EurekaMain {
    public static void main(String[] args){
        SpringApplication.run(EurekaMain.class,args);
    }
}
