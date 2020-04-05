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

    public CrosFilter(String allowDomarrrrin) {
        this.allowDomain = allowDomain;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(HttpMethod.OPTIONS.toString().equals(request.getMethod())){
            response.setStatus(200);
            response.setHeader("Access-Control-Allow-Origin", allowDomain);
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Allow-Headers", "Authorization,Origin, X-Requested-With, Content-Type, Accept");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Expose-Headers", "Authorization");
            response.getWriter().write("");
            response.getWriter().close();
            return;
        }else{
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
                response.setHeader("Access-Control-Expose-Headers", "Authorization");

            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
