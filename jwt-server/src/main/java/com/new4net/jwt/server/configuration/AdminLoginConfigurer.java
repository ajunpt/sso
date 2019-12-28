package com.new4net.jwt.server.configuration;

import com.new4net.jwt.client.configuration.HttpStatusLoginFailureHandler;
import com.new4net.jwt.server.filter.AdminAuthenticationFilter;
import com.new4net.jwt.server.filter.MyUsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;

public class AdminLoginConfigurer<T extends AdminLoginConfigurer<T, B>, B extends HttpSecurityBuilder<B>> extends AbstractHttpConfigurer<T, B> {

	private AdminAuthenticationFilter authFilter;

	public AdminLoginConfigurer() {
		this.authFilter = new AdminAuthenticationFilter();
	}
	
	@Override
	public void configure(B http) throws Exception {
		authFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
		authFilter.setAuthenticationFailureHandler(new HttpStatusLoginFailureHandler());
		authFilter.setSessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy());

        AdminAuthenticationFilter filter = postProcess(authFilter);

		http.addFilterAfter(filter, LogoutFilter.class);
	}
	
	public AdminLoginConfigurer<T,B> loginSuccessHandler(AuthenticationSuccessHandler authSuccessHandler){
		authFilter.setAuthenticationSuccessHandler(authSuccessHandler);
		return this;
	}

}
