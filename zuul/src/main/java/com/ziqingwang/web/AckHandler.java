package com.ziqingwang.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AckHandler {
    @GetMapping("/ack")
    public String ack(){
        return "zuul";
    }
}
