package com.new4net.sso.server.gateway;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CrosFilter extends OncePerRequestFilter {

    private String allowDomain;

    public CrosFilter(String allowDomain) {
        this.allowDomain = allowDomain;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(StringUtils.isEmpty(allowDomain)){
            allowDomain="*";
            response.setHeader("Access-Control-Allow-Origin", allowDomain);
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Allow-Credentials", "false");
        }else {
            response.setHeader("Access-Control-Allow-Origin", allowDomain);
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }
        if(HttpMethod.OPTIONS.equals(request.getMethod())){
            response.setStatus(200);
            return;
        }



        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
