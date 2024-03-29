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

    private String allowOrigin;

    public CrosFilter(String allowOrigin) {
        this.allowOrigin = allowOrigin;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "Authorization,Origin, X-Requested-With, Content-Type, Accept");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Expose-Headers", "Authorization");

        String origin=request.getHeader("Origin");


        if(StringUtils.isEmpty(origin)){
            if(StringUtils.isEmpty(allowOrigin)){
                response.setHeader("Access-Control-Allow-Origin", "*");
            }else {
                response.setHeader("Access-Control-Allow-Origin", allowOrigin);
            }
        }else{
            String s1 = origin.startsWith("http://")?origin.substring(7).trim():origin.startsWith("https://")?origin.substring(8).trim():origin;
            if(StringUtils.isEmpty(allowOrigin)){
                response.setHeader("Access-Control-Allow-Origin", "*");
            }else {
                String s2 = allowOrigin.startsWith("http://")? allowOrigin.substring(7).trim(): allowOrigin.startsWith("https://")? allowOrigin.substring(8).trim(): allowOrigin;
                if(s1.contains(s2)){
                    response.setHeader("Access-Control-Allow-Origin", origin);
                }else {
                    response.setHeader("Access-Control-Allow-Origin", allowOrigin);
                }
            }
        }
        if(HttpMethod.OPTIONS.toString().equals(request.getMethod())){
            response.setStatus(200);
            return;
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
