package com.new4net.sso.client;

import com.new4net.jwt.client.configuration.Constants;
import com.new4net.jwt.client.configuration.JwtClientProperties;
import com.new4net.sso.api.AuthService;
import com.new4net.sso.api.LoginService;
import com.new4net.sso.api.ModuleService;
import com.new4net.sso.api.dto.*;
import com.new4net.util.AjaxMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class ClientInit implements InitializingBean {

    @Autowired
    private JwtClientProperties jwtClientProperties;

    @Autowired
    private ModuleService moduleService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private AuthService authService;

    @Override

    public void afterPropertiesSet() throws Exception {

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("username", "sysadmin");
        requestBody.add("password", "sysadmin");

        try {
            AjaxMsg ajaxMsg = loginService.loginByAdmin(requestBody.toSingleValueMap());
            if (AjaxResult.SUCCESS.equals(ajaxMsg.getCode())) {
                Constants.Authorization.set((String) ajaxMsg.getObj());

            } else if ("-1".equals(ajaxMsg.getCode())) {
                loginService.logoutByToken(Constants.Authorization.get());
            } else {
                throw new Exception(ajaxMsg.getMsg());

            }
        } catch (HttpStatusCodeException e) {
            throw new Exception(e);
        }

        Set<AuthorityRelationInfo> authorityRelationInfos = new HashSet<>();

        Set<Auth> authSet = new HashSet<>();


        authSet.add(Auth.builder().authority("ROLE_CLIENTUSER").authorityName("客户端一般用户").remark("客户端一般用户")
                .authorityRelationInfos(authorityRelationInfos).build());

        authorityRelationInfos = new HashSet<>();
        authorityRelationInfos.add(AuthorityRelationInfo.builder().superAuthName("客户端VIP用户").subAuthName("客户端一般用户").superAuthCode("ROLE_CLIENTVIP").subAuthCode("ROLE_CLIENTUSER").build());
        authSet.add(Auth.builder().authority("ROLE_CLIENTVIP").authorityName("客户端VIP用户").remark("客户端VIP用户")
                .authorityRelationInfos(authorityRelationInfos).build());

        //模块注册
        ModuleInfo module = ModuleInfo.builder()
                .moduleName(jwtClientProperties.getModuleName())
                .superModuleName("SSO")
                .remark("CLIENT测试模块")
                .enable(true)
                .auths(authSet)
                .build();

        AjaxMsg ajaxMsg = moduleService.regModule(module);

        if (ModuleOperateResult.REG_SUCCESS.equals(ajaxMsg.getCode())) {
            log.info("CLIENT测试模块注册成功");
        } else if (ModuleOperateResult.REG_FAIL_EXISITS.equals(ajaxMsg.getCode())) {
            log.info("CLIENT测试模块注册失败，模块已存在");
        } else {
           log.info("CLIENT测试模块注册失败，其他错误");
        }

        //挂载权限

        Auth auth = authService.findByAuthorityCode("ROLE_USER");
        Set<AuthorityRelationInfo> authInfo = auth.getAuthorityRelationInfos();
        if (authInfo == null) {
            authInfo = new HashSet<>();
        }
        authInfo.add(AuthorityRelationInfo.builder().superAuthCode(auth.getAuthority()).superAuthName(auth.getAuthorityName()).subAuthCode("ROLE_CLIENTUSER").subAuthName("客户端一般用户").build());
        authService.saveAuth(auth);

        auth = authService.findByAuthorityCode("ROLE_VIP1");
        authInfo = auth.getAuthorityRelationInfos();
        if (authInfo == null) {
            authInfo = new HashSet<>();
        }
        authInfo.add(AuthorityRelationInfo.builder().superAuthCode(auth.getAuthority()).superAuthName(auth.getAuthorityName()).subAuthCode("ROLE_CLIENTVIP").subAuthName("客户端VIP用户").build());
        authService.saveAuth(auth);

    }
}
