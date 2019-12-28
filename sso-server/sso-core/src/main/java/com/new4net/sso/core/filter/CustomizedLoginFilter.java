package com.new4net.sso.core.filter;

import com.new4net.sso.core.handler.MyAuthenticationSuccessHandler;
import com.new4net.sso.core.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;

@Component
public class CustomizedLoginFilter extends OncePerRequestFilter {
    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        HttpSession session = request.getSession();

        String fromURL = request.getParameter(Constants.RedirectUrl);

        if (!StringUtils.isEmpty(fromURL)) {
            fromURL = URLDecoder.decode(fromURL, "utf-8");
        }
        if (!StringUtils.isEmpty(fromURL)) {
            session.setAttribute(Constants.RedirectUrl, fromURL);

        }

//        if (!"/login".equals(request.getRequestURI())&&SecurityContextHolder.getContext().getAuthentication()!=null&&SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
//            myAuthenticationSuccessHandler.onAuthenticationSuccess(request, response,SecurityContextHolder.getContext().getAuthentication());
//        } else {
//
//        }
        doFilter(request, response, filterChain);
    }
}
