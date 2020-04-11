package com.ziqingwang.feature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan
@EnableSwagger2
public class App {
    public static void main(String[] args){
        SpringApplication.run(App.class,args);
    }
}
