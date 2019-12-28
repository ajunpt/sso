package com.new4net.jwt.server.configuration;

import com.new4net.sso.api.dto.UserInfo;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface DaoUserDetailsService extends UserDetailsService{

    String saveUserLoginInfo(UserInfo userInfo1);

    void deleteUserLoginInfo(String username);

}
