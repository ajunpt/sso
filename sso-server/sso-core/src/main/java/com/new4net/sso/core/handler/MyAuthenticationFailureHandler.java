package com.new4net.sso.core.handler;

import com.alibaba.fastjson.JSON;
import lombok.Setter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Setter
    private String failureUrl;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter writer = response.getWriter();
//            writer.write(JSON.toJSONString(genToken()));
        }else{
            response.sendRedirect(failureUrl);
        }

    }

//    private com.new4net.sso.api.dto.Authentication genToken() {
//        return com.new4net.sso.api.dto.Authentication.builder().allowed(false).test();
//    }
}
