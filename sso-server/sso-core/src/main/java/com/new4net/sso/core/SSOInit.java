package com.new4net.sso.core;

import com.new4net.jwt.client.configuration.JwtClientProperties;
import com.new4net.sso.api.dto.Auth;
import com.new4net.sso.api.dto.AuthorityRelationInfo;
import com.new4net.sso.api.dto.ModuleInfo;
import com.new4net.sso.api.dto.ModuleOperateResult;
import com.new4net.sso.core.entity.Authority;
import com.new4net.sso.core.entity.User;
import com.new4net.sso.core.repo.AuthorityReposity;
import com.new4net.sso.core.repo.UserReposity;
import com.new4net.sso.core.service.impl.ModuleServiceImpl;
import com.new4net.util.AjaxMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
@Slf4j
@Component
public class SSOInit implements InitializingBean {

    @Autowired
    private JwtClientProperties jwtClientProperties;

    @Autowired
    private ModuleServiceImpl moduleService;
    @Autowired
    private UserReposity userReposity;
    @Autowired
    private AuthorityReposity authorityReposity;
    @Override
    public void afterPropertiesSet() throws Exception {
        Set<AuthorityRelationInfo> authorityRelationInfos = new HashSet<>();

        Set<Auth> authSet = new HashSet<>();

        authorityRelationInfos.add(AuthorityRelationInfo.builder().superAuthCode("ROLE_SYSTEMADMIN").superAuthName("系统管理员").subAuthCode("ROLE_MODULEADMIN").subAuthName("模块管理员").build());
        authSet.add(Auth.builder().authority("ROLE_SYSTEMADMIN").authorityName("系统管理员").remark("系统管理员")
                .authorityRelationInfos(authorityRelationInfos).build());

        authorityRelationInfos = new HashSet<>();
        authorityRelationInfos.add(AuthorityRelationInfo.builder().superAuthName("模块管理员").subAuthName("一般用户").superAuthCode("ROLE_MODULEADMIN").subAuthCode("ROLE_USER").build());
        authSet.add(Auth.builder().authority("ROLE_MODULEADMIN").authorityName("模块管理员").remark("模块管理员")
                .authorityRelationInfos(authorityRelationInfos).build());


        authorityRelationInfos = new HashSet<>();
        authorityRelationInfos.add(AuthorityRelationInfo.builder().superAuthCode("VIP2").superAuthName("VIP二级会员").subAuthName("一般用户").subAuthCode("ROLE_USER").build());
        authSet.add(Auth.builder().authority("ROLE_VIP2").authorityName("VIP二级会员").remark("VIP二级会员")
                .authorityRelationInfos(authorityRelationInfos).build());

        authorityRelationInfos = new HashSet<>();
        authorityRelationInfos.add(AuthorityRelationInfo.builder().superAuthCode("VIP1").superAuthName("VIP一级会员").subAuthName("一般用户").subAuthCode("ROLE_USER").build());
        authSet.add(Auth.builder().authority("ROLE_VIP1").authorityName("VIP一级会员").remark("VIP一级会员")
                .authorityRelationInfos(authorityRelationInfos).build());


        authSet.add(Auth.builder().authority("ROLE_USER").authorityName("一般用户").remark("一般用户").build());


        ModuleInfo module = ModuleInfo.builder()
                .moduleName(jwtClientProperties.getModuleName())
                .remark("SSO系统核心模块")
                .enable(true)
                .auths(authSet)
                .build();


        AjaxMsg ajaxMsg = moduleService.regModule(module);
        if(ModuleOperateResult.REG_SUCCESS.equals(ajaxMsg.getCode())){
            log.info("核心模块注册成功");
        }else if(ModuleOperateResult.REG_FAIL_EXISITS.equals(ajaxMsg.getCode())) {
            log.info("核心模块注册失败，模块已存在");
        }else{
            log.info("核心模块注册失败，其他错误");
        }
        if(userReposity.findByUsername("sysadmin")==null){
            HashSet<Authority> set = new HashSet<>();
            set.add(authorityReposity.findById("ROLE_SYSTEMADMIN").get());
            set.add(authorityReposity.findById("ROLE_MODULEADMIN").get());
            String password = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("sysadmin");
            User user = User.builder().username("sysadmin").password(password).accountNonExpired(true)
                    .accountNonLocked(true).authorities(set).credentialsNonExpired(true).enable(true).validTime(new Date())
                    .build();
            userReposity.save(user);
            log.info("系统管理员注册成功!");
        }

    }
}
