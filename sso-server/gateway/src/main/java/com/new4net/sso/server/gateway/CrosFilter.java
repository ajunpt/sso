package com.new4net.sso.server.gateway;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.new4net.util.AjaxMsg;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

public class CrosFilter extends OncePerRequestFilter {

    private String  allowDomain;

    public CrosFilter(String allowDomain) {
        this.allowDomain = allowDomain;
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(HttpMethod.OPTIONS.equals(request.getMethod())||HttpMethod.GET.equals(request.getMethod())){
            response.setHeader("Access-Control-Allow-Origin", allowDomain );
            response.setHeader("Access-Control-Allow-Methods", "*" );
            response.setHeader("Access-Control-Allow-Headers", "*" );
            response.setHeader("Access-Control-Allow-Credentials", "true" );

        }
        filterChain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
