package com.new4net.jwt.client.service;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.new4net.jwt.client.configuration.Constants;
import com.new4net.jwt.client.configuration.JwtAuthenticationToken;
import com.new4net.sso.api.JwtUserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.NonceExpiredException;

import java.util.Calendar;
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private JwtUserService userService;

    public JwtAuthenticationProvider() {

    }

    public JwtAuthenticationProvider(JwtUserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        DecodedJWT jwt = ((JwtAuthenticationToken) authentication).getToken();
        if (jwt.getExpiresAt().before(Calendar.getInstance().getTime()))
            throw new NonceExpiredException("Token expires");
        String username = jwt.getSubject();
        Constants.Authorization.set(jwt.getToken());
        UserDetails user = userService.loadUserByUsername(username+";"+Constants.ModuleName);
        if (user == null || user.getPassword() == null)
            throw new NonceExpiredException("Token expires");
        else {
            JwtAuthenticationToken token = new JwtAuthenticationToken(user, jwt, user.getAuthorities());
            return token;
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(JwtAuthenticationToken.class);
    }

}
