package com.new4net.sso.api;

import com.new4net.sso.api.dto.Auth;
import com.new4net.sso.api.dto.ModuleInfo;
import com.new4net.util.AjaxMsg;
import com.new4net.util.Page;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(value = "sso-server")
public interface ModuleService {
    @RequestMapping(value = "/regModule", method = RequestMethod.POST)
    public AjaxMsg regModule(@RequestBody ModuleInfo module);

    @RequestMapping(value = "/removeModule", method = RequestMethod.GET)
    public AjaxMsg removeModule(@RequestParam("moduleName") String moduleId);

    @RequestMapping(value = "/listModules", method = RequestMethod.POST)
    public Page<ModuleInfo> listModules(@RequestBody Map<String, Object> params);

    @RequestMapping("/listAllModules")
    public List<ModuleInfo> listAllModules();

    @RequestMapping(value = "/modifyModuleInfo", method = RequestMethod.POST)
    public AjaxMsg modifyModuleInfo(@RequestBody ModuleInfo m);
}

