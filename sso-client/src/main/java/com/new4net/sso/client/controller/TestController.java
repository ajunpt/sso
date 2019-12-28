package com.new4net.sso.client.controller;

import com.new4net.sso.api.JwtUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {
    @Autowired
    private JwtUserService jwtUserService;
    @RequestMapping(value = "/test",method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String test(){
        return jwtUserService.loadUserByUsername("sysadmin").getUsername();
    }
}
