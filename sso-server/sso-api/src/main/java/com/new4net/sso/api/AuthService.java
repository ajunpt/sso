package com.new4net.sso.api;

import com.new4net.sso.api.dto.Auth;
import com.new4net.util.AjaxMsg;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "sso-server")
public interface AuthService {
    @RequestMapping(value = "/findByAuthorityCode/{authorityCode}",method = RequestMethod.GET)
    public Auth findByAuthorityCode(@PathVariable("authorityCode")String authority);
    @RequestMapping(value = "/saveAuthorityRelations",method = RequestMethod.POST)
    public AjaxMsg saveAuthorityRelations(@RequestBody Auth auth);
    @RequestMapping(value = "/saveAuth",method = RequestMethod.POST)
    public AjaxMsg saveAuth(@RequestBody Auth auth);
    @RequestMapping(value = "/removeAuth", method = RequestMethod.GET)
    public AjaxMsg removeAuth(@RequestParam("authorityCode") String authorityCode);
}
