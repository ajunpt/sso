package com.new4net.jwt.server.configuration;

import com.alibaba.fastjson.JSON;
import com.new4net.sso.api.dto.Auth;
import com.new4net.sso.api.dto.UserInfo;
import com.new4net.util.AjaxMsg;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

public class JsonLoginSuccessHandler implements AuthenticationSuccessHandler {
	
	private DaoUserDetailsService daoUserDetailsService;
	
	public JsonLoginSuccessHandler(DaoUserDetailsService daoUserDetailsService) {
		this.daoUserDetailsService = daoUserDetailsService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

	    Object userInfo =  authentication.getPrincipal();
	    UserInfo userInfo1 = null;
        if(userInfo instanceof  UserInfo){
            userInfo1 = (UserInfo) userInfo;
        }else{
            UserDetails userDetails = (UserDetails) userInfo;
            userInfo1=UserInfo.builder().username(userDetails.getUsername())
                    .accountNonExpired(userDetails.isAccountNonExpired())
                    .accountNonLocked(userDetails.isAccountNonLocked())
                    .credentialsNonExpired(userDetails.isCredentialsNonExpired())
                    .enable(userDetails.isEnabled())
                    .build();
        }
		String token = daoUserDetailsService.saveUserLoginInfo(userInfo1);
		response.setHeader("Authorization", token);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();

        writer.write(JSON.toJSONString(new AjaxMsg("1","登陆成功",SecurityContextHolder.getContext().getAuthentication().getAuthorities())));
	}
	
}
