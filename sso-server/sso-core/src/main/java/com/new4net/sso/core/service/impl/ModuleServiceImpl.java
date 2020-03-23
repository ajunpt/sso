package com.new4net.sso.core.service.impl;

import com.new4net.jwt.client.configuration.JwtClientProperties;
import com.new4net.sso.api.dto.Auth;
import com.new4net.sso.api.dto.ModuleInfo;
import com.new4net.sso.api.dto.ModuleOperateResult;
import com.new4net.sso.core.dao.BaseDao;
import com.new4net.sso.core.entity.Authority;
import com.new4net.sso.core.entity.AuthorityRelation;
import com.new4net.sso.core.entity.Module;
import com.new4net.sso.core.entity.Role;
import com.new4net.sso.core.repo.AuthorityReposity;
import com.new4net.sso.core.repo.ModuleReposity;
import com.new4net.sso.core.repo.RoleReposity;
import com.new4net.util.AjaxMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ModuleServiceImpl extends BaseServiceImpl<Module> {
    @Resource(name="moduleDao")
    public void setBaseDao(BaseDao<Module> baseDao) {
        super.setBaseDao(baseDao);
    }
    public ModuleServiceImpl(){
    }
    @Autowired
    private ModuleReposity moduleReposity;
    @Autowired
    private RoleReposity roleReposity;
    @Autowired
    private AuthorityReposity authorityReposity;

    public AjaxMsg regModule(ModuleInfo module) {
        Module m=moduleReposity.findByModuleName(module.getModuleName());
        if(m!=null){
            return new AjaxMsg(ModuleOperateResult.REG_FAIL_EXISITS,"模块已存在");
        }
        m = Module.builder().moduleName(module.getModuleName()).superModuleName(module.getSuperModuleName()).enable(module.isEnable()).remark(module.getRemark()).build();

        moduleReposity.save(m);

        Set<Auth> authSet = module.getAuths();


        if(authSet!=null){
            Set<Auth> set = new HashSet<>();
            for(Iterator<Auth> it = authSet.iterator(); it.hasNext();){
                Auth auth = it.next();
                if(auth!=null&&auth.getAuthority()!=null&&auth.getAuthority().startsWith("ROLE")){
                    Role role = new Role();
                    role.setModule(m);
                    role.setRemark(auth.getRemark());
                    role.setRoleCode(auth.getAuthority());
                    role.setRoleName(auth.getAuthorityName());
                    role.setAuthorityRelations(auth.getAuthorityRelationInfos()==null?null:auth.getAuthorityRelationInfos().stream().map(authorityRelationInfo -> {
                        return AuthorityRelation.builder().id(authorityRelationInfo.getSuperAuthCode()+authorityRelationInfo.getSubAuthCode()).superAuthCode(authorityRelationInfo.getSuperAuthCode()).superAuthName(authorityRelationInfo.getSuperAuthName()).subAuthName(authorityRelationInfo.getSubAuthName()).subAuthCode(authorityRelationInfo.getSubAuthCode()).build();}).collect(Collectors.toSet()));
                    roleReposity.save(role);
                }else {
                    Authority authority = Authority.builder().authorityCode(auth.getAuthority()).module(m)
                            .authorityRelations(auth.getAuthorityRelationInfos()==null?null:auth.getAuthorityRelationInfos().stream().map(authorityRelationInfo -> {
                                return AuthorityRelation.builder().id(authorityRelationInfo.getSuperAuthCode()+authorityRelationInfo.getSubAuthCode()).superAuthCode(authorityRelationInfo.getSuperAuthCode()).superAuthName(authorityRelationInfo.getSuperAuthName()).subAuthCode(authorityRelationInfo.getSubAuthCode()).subAuthName(authorityRelationInfo.getSubAuthName()).build();}).collect(Collectors.toSet()))
                            .remark(auth.getRemark()).build();
                    authorityReposity.save(authority);
                }

            }

        }

        return new AjaxMsg(ModuleOperateResult.REG_SUCCESS,"模块注册成功");
    }

    @RequestMapping("listAllModules")
    public List<ModuleInfo> listAllModules(){
        List<Module> modules = moduleReposity.findAll(new Sort(Sort.Direction.ASC,"createTime"));
        List<Authority> authorities = authorityReposity.findAll();
        List<ModuleInfo> moduleInfos = new ArrayList<>();
        if(modules!=null){
            for(Module module:modules){
                ModuleInfo moduleInfo = module.buildModuleInfo();
                if(authorities!=null){
                    moduleInfo.setAuths(authorities.stream().filter(authority -> {
                        return authority.getModule() != null && module.getModuleName() != null && module.getModuleName().equals(authority.getModule().getModuleName());
                    }).map(authority -> {
                        Auth auth = authority.buildAuth();
                        auth.setModuleName(moduleInfo.getModuleName());
                        return auth ;
                    }).collect(Collectors.toSet()));
                    moduleInfos.add(moduleInfo);
                }
            }
        }

        return moduleInfos ;
    }
    @Autowired
    private JwtClientProperties jwtClientProperties;
    @Transactional
    public AjaxMsg modifyModuleInfo(ModuleInfo module){

        if(jwtClientProperties.getModuleName().equals(module.getModuleName())){
            if(!module.isEnable()){
                return new AjaxMsg("0","不能禁用核心模块");
            }
            if(module.getSuperModuleName()!=null&&!"".equals(module.getSuperModuleName())){

                return new AjaxMsg("0","修改核心模块的上级模块");
            }

        }
        Module m = moduleReposity.findByModuleName(module.getModuleName());
        m.setEnable(module.isEnable());
        m.setSuperModuleName(module.getSuperModuleName());
        m.setRemark(module.getRemark());
        moduleReposity.save(m);

        return new AjaxMsg("1","保存成功");
    };
}
