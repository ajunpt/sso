package com.new4net.sso.core.controller;

import com.new4net.sso.api.AuthService;
import com.new4net.sso.api.dto.Auth;
import com.new4net.sso.core.entity.Authority;
import com.new4net.sso.core.entity.AuthorityRelation;
import com.new4net.sso.core.entity.Module;
import com.new4net.sso.core.entity.User;
import com.new4net.sso.core.repo.AuthorityRelationReposity;
import com.new4net.sso.core.repo.AuthorityReposity;
import com.new4net.sso.core.repo.ModuleReposity;
import com.new4net.sso.core.repo.UserReposity;
import com.new4net.sso.core.service.AuthorityService;
import com.new4net.sso.core.service.impl.UserService;
import com.new4net.util.AjaxMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
public class AuthController implements AuthService {
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private AuthorityReposity authorityReposity;
    @Autowired
    private ModuleReposity moduleReposity;

    @Override
    public Auth findByAuthorityCode(String authority) {
        Authority authority1 = authorityService.findById(authority);

        return authority1 == null ? null : authority1.getAuth();
    }

    @Override
    @PreAuthorize("hasRole('ROLE_MODULEADMIN')||hasRole('ROLE_SYSTEMADMIN')")

    public AjaxMsg saveAuthorityRelations(Auth auth) {

        Authority authority = authorityService.findById(auth.getAuthority());

        authority.setAuthorityRelations(auth.getAuthorityRelationInfos() == null ? authority.getAuthorityRelations() : auth.getAuthorityRelationInfos().stream().map(authorityRelationInfo -> {
            return AuthorityRelation.builder().id(auth.getAuthority() + authorityRelationInfo.getSubAuthCode())
                    .subAuthName(authorityRelationInfo.getSubAuthName()).superAuthName(auth.getAuthorityName())
                    .superAuthCode(auth.getAuthority()).subAuthCode(authorityRelationInfo.getSubAuthCode()).build();
        }).collect(Collectors.toSet()));
        authorityReposity.save(authority);
        saveRelation(authority);
        return new AjaxMsg("1", "保存成功");
    }

    @Override
    @PreAuthorize("hasRole('ROLE_MODULEADMIN')||hasRole('ROLE_SYSTEMADMIN')")

    public AjaxMsg saveAuth(Auth auth) {
        Authority authority = null;
        Module module = moduleReposity.findByModuleName(auth.getModuleName());

        if((authority=authorityService.findById(auth.getAuthority()))==null){
            authority = new Authority();
            authority.setAuthorityCode(auth.getAuthority());
        }
        if(module!=null){
            authority.setModule(module);
        }
        if(auth.getAuthorityName()!=null){
            authority.setAuthorityName(auth.getAuthorityName());
        }

        if(auth.getRemark()!=null){
            authority.setRemark(auth.getRemark());
        }
        if(auth.getAuthorityRelationInfos()!=null){
            authority.setAuthorityRelations(auth.getAuthorityRelationInfos() == null ? authority.getAuthorityRelations() : auth.getAuthorityRelationInfos().stream().map(authorityRelationInfo -> {
                return AuthorityRelation.builder().id(authorityRelationInfo.getSuperAuthCode() + authorityRelationInfo.getSubAuthCode()).subAuthName(authorityRelationInfo.getSubAuthName()).superAuthName(authorityRelationInfo.getSuperAuthName())
                        .superAuthCode(authorityRelationInfo.getSuperAuthCode()).subAuthCode(authorityRelationInfo.getSubAuthCode()).build();
            }).collect(Collectors.toSet()));
        }

        authorityReposity.save(authority);
        saveRelation(authority);



        return new AjaxMsg("1", "保存成功");
    }

    private void saveRelation(Authority authority) {
        List<Authority> authorities = authorityService.findByHQL("select a From Authority as a join a.authorityRelations r  where r.superAuthCode=?0",authority.getAuthorityCode());

        if(authorities!=null){
            authorities.stream().forEach(authority1-> {
                if(authority1!=null&&authority1.getAuthorityRelations()!=null){
                    authority1.getAuthorityRelations().stream().forEach(relation->{
                        if(relation.getSuperAuthCode()!=null&&relation.getSuperAuthCode().equals(authority.getAuthorityCode())){
                            relation.setSuperAuthName(authority.getAuthority());
                            relation.setSuperAuthName(authority.getAuthorityName());
                        }

                    });
                }

                authorityReposity.save(authority1);
            });
        }
        authorities = authorityService.findByHQL("select a From Authority as a join a.authorityRelations r  where r.subAuthCode=?0",authority.getAuthorityCode());

        if(authorities!=null){
            authorities.stream().forEach(authority1-> {
                if(authority1!=null&&authority1.getAuthorityRelations()!=null){
                    authority1.getAuthorityRelations().stream().forEach(relation->{
                        if(relation.getSubAuthCode()!=null&&relation.getSubAuthCode().equals(authority.getAuthorityCode())){
                            relation.setSubAuthCode(authority.getAuthority());
                            relation.setSubAuthName(authority.getAuthorityName());
                        }
                    });
                }

                authorityReposity.save(authority1);
            });
        }
    }


    @Autowired
    private AuthorityRelationReposity authorityRelationReposity;
    @Autowired
    private UserService userService;
    @Autowired
    private UserReposity userReposity;

    @Override
    @PreAuthorize("hasRole('ROLE_MODULEADMIN')||hasRole('ROLE_SYSTEMADMIN')")

    public AjaxMsg removeAuth(String authorityCode) {
        if("ROLE_SYSTEMADMIN".equals(authorityCode)){
            return new AjaxMsg("0", "不能删除系统管理员");
        }
        List<Authority> authorities = authorityService.findByHQL("select a From Authority as a join a.authorityRelations r  where r.subAuthCode=?0",authorityCode);

        if(authorities!=null){
            authorities.stream().forEach(authority1-> {
                if(authority1!=null&&authority1.getAuthorityRelations()!=null){
                    authority1.setAuthorityRelations(authority1.getAuthorityRelations().stream().filter(relation->{
                        return relation.getSubAuthCode()==null||!relation.getSubAuthCode().equals(authorityCode);
                    }).collect(Collectors.toSet()));
                }

                authorityReposity.save(authority1);
            });
        }
        Authority authority = authorityReposity.findById(authorityCode).get();
        List<AuthorityRelation> authorityRelations = authorityRelationReposity.findBySubAuthCode(authorityCode);
        if(authorityRelations!=null){
            authorityRelations.stream().forEach(authorityRelation -> {
                authorityRelationReposity.delete(authorityRelation);
            });
        }

        List<User> users = userService.findByHQL("select u From User as u join u.authorities as a where a.authorityCode=?0",authorityCode);

        if(users!=null){
            users.stream().forEach((User user) -> {
                if(user!=null&&user.getAuthorities()!=null){
                    user.setAuthorities(user.getAuthorities().stream().filter(authority1->{
                        return !authorityCode.equals(authority1.getAuthority());
                    }).collect(Collectors.toSet()));
                }

                userReposity.save(user);
            });
        }

        authorityReposity.deleteById(authorityCode);
        return new AjaxMsg("1", "删除成功");
    }
}
