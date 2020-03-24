package com.new4net.sso.core.controller;

import com.auth0.jwt.JWT;
import com.new4net.jwt.client.configuration.JwtAuthenticationToken;
import com.new4net.jwt.client.configuration.JwtClientProperties;
import com.new4net.jwt.server.configuration.DaoUserDetailsService;
import com.new4net.sso.api.LoginService;
import com.new4net.sso.api.dto.UserInfo;
import com.new4net.util.AjaxMsg;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController

public class LoginController implements LoginService {
    @Autowired
    private RedisTemplate redisTemplate;



    @Autowired
    private JwtClientProperties jwtClientProperties;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        return "test";
    }
    @Autowired
    private DaoUserDetailsService daoUserDetailsService;
    @Override
    public AjaxMsg loginByAdmin(@RequestBody Map<String, String> params) {
        String username =  params.get("username");

        String salt = (String) redisTemplate.opsForValue().get("token:" +username);
        if(salt==null|| StringUtils.isEmpty(salt)){
            UserInfo userInfo = (UserInfo) daoUserDetailsService.loadUserByUsername(username+";"+ jwtClientProperties.getModuleName());
            if(userInfo!=null&& passwordEncoder.matches(params.get("password"),userInfo.getPassword())){
                String token = daoUserDetailsService.saveUserLoginInfo(userInfo);
                return new AjaxMsg("1","登陆成功!",token);
            }else{
                return new AjaxMsg("0","登陆失败");
            }
        }else {

            return new AjaxMsg("-1","已登陆，请注销后重试");
        }
    }

    @Override
    public AjaxMsg logoutByToken(@RequestParam("token") String token) {
        if(token == null)
            return new AjaxMsg("0","注销失败");
        JwtAuthenticationToken authToken = new JwtAuthenticationToken(JWT.decode(token));

        UserDetails user = (UserDetails)authToken.getPrincipal();
        if(user!=null && user.getUsername()!=null){
            daoUserDetailsService.deleteUserLoginInfo(user.getUsername());
            return new AjaxMsg("1","注销成功");
        }
        return new AjaxMsg("0","注销失败");
    }
}
