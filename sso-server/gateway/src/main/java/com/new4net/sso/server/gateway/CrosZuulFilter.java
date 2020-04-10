package com.new4net.sso.server.gateway;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class CrosZuulFilter  extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE ;
    }

    @Override
    public int filterOrder() {
        return Integer.MIN_VALUE+1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        log.info("开始");
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();
        log.info(request.getMethod());
        log.info(request.getRequestURI());
        if(HttpMethod.OPTIONS.equals(request.getMethod())){
            response.setStatus(200);
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Expose-Headers", "Authorization");
            log.info("开始1");
            try {
                response.getWriter().write("");
                response.getWriter().close();
            } catch (IOException e) {
                throw new ZuulException(e,403,e.getMessage());
            }
            log.info("开始2");
            throw new ZuulException("",200,"");
        }
        return null;
    }
}
