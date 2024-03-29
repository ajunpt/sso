package com.new4net.jwt.server.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AdminAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private Map<Long,AtomicInteger> maxAttempt = new ConcurrentHashMap<>();

	public AdminAuthenticationFilter() {
		super(new AntPathRequestMatcher("/loginByAdmin","POST" ));
	}
	
	@Override
	public void afterPropertiesSet() {
		Assert.notNull(getAuthenticationManager(), "authenticationManager must be specified");
		Assert.notNull(getSuccessHandler(), "AuthenticationSuccessHandler must be specified");
		Assert.notNull(getFailureHandler(), "AuthenticationFailureHandler must be specified");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
	    long l = System.currentTimeMillis()/1000*3;
	    AtomicInteger atomicInteger = maxAttempt.get(l);
	    if(atomicInteger==null){
	        maxAttempt.clear();
	        maxAttempt.put(l,new AtomicInteger(5));
        }else{
            int i = atomicInteger.decrementAndGet();
            if(i<0){
                throw new AuthenticationException("达到最大认证次数"){

                };
            }
        }

		String body = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
		String username = null, password = null;
		if(StringUtils.hasText(body)) {
		    JSONObject jsonObj = JSON.parseObject(body);
		    username = jsonObj.getString("username");
		    password = jsonObj.getString("password");
		}	

		if (username == null) 
			username = "";
		if (password == null)
			password = "";
		username = username.trim();

		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
				username, password);
		
		return this.getAuthenticationManager().authenticate(authRequest);
	}
}
