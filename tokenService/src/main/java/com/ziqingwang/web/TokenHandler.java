package com.ziqingwang.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wangz
 */
@RestController
public class TokenHandler {
    Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/verify")
    public String ack(@CookieValue(name="token",defaultValue = "10086")String token, HttpServletResponse response){
        logger.warn("tokenValue: [{}]",token);
        Cookie cookie = new Cookie("token",(Long.valueOf(token)+1)+"");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60);
        response.addCookie(cookie);
        return "true";
    }

    /**
     * 测试超时
     *      验证zuul的超时设置
     * @return
     */
    @GetMapping("/timeOut")
    public String timeOut(){
        try{
            Thread.sleep(500);
        }catch (InterruptedException e){
            // ignore
        }
        return "竟然等这么久";
    }
}
