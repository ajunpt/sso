package com.new4net.sso.core.controller;

import com.new4net.jwt.client.configuration.JwtClientProperties;
import com.new4net.sso.api.ModuleService;
import com.new4net.sso.api.dto.ModuleInfo;
import com.new4net.sso.api.dto.ModuleOperateResult;
import com.new4net.sso.core.entity.Authority;
import com.new4net.sso.core.entity.Module;
import com.new4net.sso.core.repo.AuthorityRelationReposity;
import com.new4net.sso.core.repo.AuthorityReposity;
import com.new4net.sso.core.repo.ModuleReposity;
import com.new4net.sso.core.service.impl.ModuleServiceImpl;
import com.new4net.util.AjaxMsg;
import com.new4net.util.Page;
import com.new4net.util.PageConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class ModuleController implements ModuleService {
    @Autowired
    private ModuleReposity moduleReposity;

    @Autowired
    private AuthorityReposity authorityReposity;
    @Autowired
    private AuthorityRelationReposity authorityRelationReposity;


    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_MODULEADMIN')||hasRole('ROLE_SYSTEMADMIN')")
    public AjaxMsg regModule(@RequestBody ModuleInfo module) {
        return moduleService.regModule(module);
    }
    @Autowired
    private JwtClientProperties jwtClientProperties;
    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_MODULEADMIN')||hasRole('ROLE_SYSTEMADMIN')")
    public AjaxMsg removeModule(@RequestParam("moduleName")String moduleName) {
        if(jwtClientProperties.getModuleName().equals(moduleName)){
            return new AjaxMsg(ModuleOperateResult.DEL_FAIL_OTHER,"删除失败，不能删除核心模块!");
        }
        Module m=moduleReposity.findByModuleName(moduleName);
        if(m!=null){
            Set<Authority> authorities = authorityReposity.findByModule(m);
            for(Authority authority :authorities)
                authorityRelationReposity.deleteBySubAuthCode(authority.getAuthorityCode());

            authorityReposity.deleteByModule(m);
            moduleReposity.delete(m);
            return new AjaxMsg(ModuleOperateResult.DEL_SUCCESS,"删除成功!");
        }
        return new AjaxMsg(ModuleOperateResult.DEL_FAIL_NOT_EXISITS,"模块不存在");
    }
    @Autowired
    private ModuleServiceImpl moduleService;
    @Override
    @PreAuthorize("hasRole('ROLE_MODULEADMIN')||hasRole('ROLE_SYSTEMADMIN')")
    @Transactional
    public Page<ModuleInfo> listModules(@RequestBody Map<String, Object> params){
        int pageNo = Integer.parseInt(String.valueOf(params.get("pageNo")));
        int pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
        String moduleName = (String) params.get("moduleName");
        Boolean enable = (Boolean) params.get("enable");
        String moduleId = (String) params.get("moduleId");
        Map<String, Object> ps = new HashMap<>();

        if (!StringUtils.isEmpty(moduleName)) {
            ps.put("moduleName||like", moduleName);

        }
        if (enable!=null) {
            ps.put("enable", enable);

        }
        if (!StringUtils.isEmpty(moduleId)) {
            Module module = moduleService.findById(moduleId);
            if (module != null) {
                ps.put("module", module);
            }
        }

        Page<Module> modulePage =moduleService.queryPage(pageNo, pageSize, ps);


        return new PageConverter<Module,ModuleInfo>().convert(modulePage,(module)->{


            return ModuleInfo.builder().enable(module.isEnable())
                    .moduleName(module.getModuleName())
                    .remark(module.getRemark()).build();
        });

    }
    @PreAuthorize("hasRole('ROLE_MODULEADMIN')||hasRole('ROLE_SYSTEMADMIN')")

    public List<ModuleInfo> listAllModules(){
       return moduleService.listAllModules();
    }
    @PreAuthorize("hasRole('ROLE_MODULEADMIN')||hasRole('ROLE_SYSTEMADMIN')")
    public AjaxMsg modifyModuleInfo(@RequestBody  ModuleInfo m){
        return moduleService.modifyModuleInfo(m);
    }
}
