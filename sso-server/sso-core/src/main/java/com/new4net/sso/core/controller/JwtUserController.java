package com.new4net.sso.core.controller;

import com.new4net.jwt.client.configuration.JwtClientProperties;
import com.new4net.jwt.server.configuration.DaoUserDetailsService;
import com.new4net.sso.api.JwtUserService;
import com.new4net.sso.api.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class JwtUserController implements JwtUserService {

    @Autowired
    private DaoUserDetailsService daoUserDetailsService;

    public String saveUserLoginInfo(UserInfo user) {
        return daoUserDetailsService.saveUserLoginInfo(user);

    }

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JwtClientProperties jwtClientProperties;

    //必须使用缓存进行改进
    @Override
    public UserInfo loadUserByUsername(String username) throws UsernameNotFoundException {

        String[] ss = username.split(";");


        String salt = (String) redisTemplate.opsForValue().get("token:" + ss[0]);


        UserInfo userInfo = (UserInfo) daoUserDetailsService.loadUserByUsername(username);

        if (salt != null&&ss.length>1&&jwtClientProperties.getModuleName().equals(ss[1])) {
            userInfo.setPassword(salt);
        }

        return userInfo;


    }

    @Override
    public void deleteUserLoginInfo(String username) {
        daoUserDetailsService.deleteUserLoginInfo(username);

    }


}
