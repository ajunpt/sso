package com.new4net.jwt.server.configuration;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenClearLogoutHandler implements LogoutHandler {
	
	private DaoUserDetailsService daoUserDetailsService;
	
	public TokenClearLogoutHandler(DaoUserDetailsService daoUserDetailsService) {
		this.daoUserDetailsService = daoUserDetailsService;
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		clearToken(authentication);
	}
	
	protected void clearToken(Authentication authentication) {
		if(authentication == null)
			return;
		UserDetails user = (UserDetails)authentication.getPrincipal();
		if(user!=null && user.getUsername()!=null)
            daoUserDetailsService.deleteUserLoginInfo(user.getUsername());
	}

}
