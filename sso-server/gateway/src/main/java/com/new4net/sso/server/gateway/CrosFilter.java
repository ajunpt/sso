package com.new4net.sso.server.gateway;

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
        if(!HttpMethod.OPTIONS.equals(request.getMethod())){
            response.setHeader("Access-Control-Allow-Origin", allowDomain);
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }


        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
