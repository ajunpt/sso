package com.new4net.sso.core.handler;

import com.alibaba.fastjson.JSON;
import com.new4net.sso.core.util.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.UUID;

@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Value("${tokenValidTime}")
    private long tokenValidTime;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession();
//
//        String redirectURL = (String) session.getAttribute(Constants.RedirectUrl) ;
//
//        com.new4net.sso.api.dto.Authentication token = (com.new4net.sso.api.dto.Authentication) session.getAttribute(Constants.Token);
//        token = StringUtils.isEmpty(token)?genToken():token;
//        if (StringUtils.isEmpty(redirectURL)) {
//            response.setCharacterEncoding("utf-8");
//            response.setContentType("application/json; charset=utf-8");
//            PrintWriter writer = response.getWriter();
//            writer.write(JSON.toJSONString(token));
//            return;
//        } else {
//            redirectURL+="?allowed="+token.isAllowed()+"&token="+token.getToken()+"&validTime="+token.getValidTime()+"&invalidTime="+token.getInvalidTime();
//            //如果request.getHeader("X-Requested-With") 返回的是"XMLHttpRequest"说明就是ajax请求，需要特殊处理
//            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
//                //告诉ajax我是重定向
//                response.setHeader("REDIRECT", "REDIRECT");
//                //告诉ajax我重定向的路径
//                response.setHeader("CONTEXTPATH", redirectURL);
//                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            } else {
//                response.sendRedirect(redirectURL);
//            }
//        }
    }
//    private com.new4net.sso.api.dto.Authentication genToken() {
//        Date date = new Date();
//        com.new4net.sso.api.dto.Authentication token = com.new4net.sso.api.dto.Authentication.builder()
//                .token(UUID.randomUUID().toString()).allowed(true).validTime(date).invalidTime(new Date(date.getTime()+tokenValidTime)).build();
//        return token;
//    }
}
