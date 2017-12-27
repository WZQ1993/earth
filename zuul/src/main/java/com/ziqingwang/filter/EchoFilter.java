package com.ziqingwang.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wangz
 */
@Component
public class EchoFilter extends ZuulFilter{
    Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public boolean shouldFilter() {
        //如果带有头信息，echo=true，则执行过滤器
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        return "true".equals(request.getHeader("echo"));
    }

    @Override
    public Object run() {
        logger.warn("执行了echo过滤器");
        return null;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return -100;
    }
}
