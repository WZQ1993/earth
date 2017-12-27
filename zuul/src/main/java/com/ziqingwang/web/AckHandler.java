package com.ziqingwang.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangz
 */
@RefreshScope
@RestController
public class AckHandler {
    @Value("${config.key}")
    private String key;

    @GetMapping("/ack")
    public String ack(){
        return "zuul";
    }

    /**
     * 测试配置中心的刷新
     * 1.应用端点/refresh,配置所在类必须使用@RefreshScope注解
     * 2.消息总线
     * @return
     */
    @GetMapping("/config/key")
    public String getKey(){
        return key;
    }
}
