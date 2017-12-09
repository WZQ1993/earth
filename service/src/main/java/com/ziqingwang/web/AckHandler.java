package com.ziqingwang.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangz
 */
@RestController
public class AckHandler {
    @GetMapping("/")
    public String ack(){
        return "service";
    }
}
