package com.new4net.sso.server.gateway;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Random;

public class CrosFilter extends OncePerRequestFilter {

    private String allowDomain;

    public CrosFilter(String allowDomain) {
        this.allowDomain = allowDomain;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(HttpMethod.OPTIONS.equals(request.getMethod())){
            response.setStatus(200);
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Expose-Headers", "Authorization");
        }else{
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Allow-Headers", "Authorization,Origin, X-Requested-With, Content-Type, Accept");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Expose-Headers", "Authorization");

            String origin=request.getHeader("Origin");


            if(StringUtils.isEmpty(origin)){
                if(StringUtils.isEmpty(allowDomain)){
                    response.setHeader("Access-Control-Allow-Origin", "*");
                }else {
                    response.setHeader("Access-Control-Allow-Origin", allowDomain);
                }
            }else{
                String s1 = origin.startsWith("http://")?origin.substring(7).trim():origin.startsWith("https://")?origin.substring(8).trim():origin;
                if(StringUtils.isEmpty(allowDomain)){
                    response.setHeader("Access-Control-Allow-Origin", "*");
                }else {
                    String s2 = allowDomain.startsWith("http://")?allowDomain.substring(7).trim():allowDomain.startsWith("https://")?allowDomain.substring(8).trim():allowDomain;
                    if(s1.contains(s2)){
                        response.setHeader("Access-Control-Allow-Origin", origin);
                    }else {
                        response.setHeader("Access-Control-Allow-Origin", allowDomain);
                    }
                }
            }

        }

        filterChain.doFilter(request, response);


    }

    @Override
    public void destroy() {

    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(()->{
            System.out.println("ok");
        });
        thread.join();
        System.out.println(thread.getId());
        System.out.println(Thread.currentThread().getId());
    }
}
