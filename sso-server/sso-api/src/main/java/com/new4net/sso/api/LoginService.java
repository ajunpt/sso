package com.new4net.sso.api;

import com.new4net.util.AjaxMsg;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@FeignClient(value = "sso-server")

public interface LoginService {
    @RequestMapping(value = "/loginByAdmin",method = RequestMethod.POST)
    public AjaxMsg loginByAdmin(@RequestBody Map<String,String> params);
    @RequestMapping(value = "/logoutByToken",method = RequestMethod.GET)
    public AjaxMsg logoutByToken(@RequestParam("token") String token);
}
