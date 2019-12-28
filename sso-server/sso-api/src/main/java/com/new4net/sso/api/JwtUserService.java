package com.new4net.sso.api;

import com.new4net.sso.api.dto.Auth;
import com.new4net.sso.api.dto.UserInfo;
import com.new4net.util.AjaxMsg;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(value = "sso-server")
public interface JwtUserService {

    @RequestMapping(value = "/loadUserByUsername/{username}",method = RequestMethod.GET)
    UserInfo loadUserByUsername(@PathVariable("username") String username ) ;
    @RequestMapping(value = "/saveUserLoginInfo",method = RequestMethod.POST)
    public String saveUserLoginInfo(@RequestBody UserInfo user);
    @RequestMapping(value = "/deleteUserLoginInfo/{username}",method = RequestMethod.GET)
    public void deleteUserLoginInfo(@PathVariable("username")String username);



}
