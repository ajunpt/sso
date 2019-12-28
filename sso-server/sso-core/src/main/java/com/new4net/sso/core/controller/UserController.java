package com.new4net.sso.core.controller;

import com.new4net.sso.api.dto.ModuleInfo;
import com.new4net.sso.api.dto.UserInfo;
import com.new4net.sso.core.entity.Authority;
import com.new4net.sso.core.entity.AuthorityRelation;
import com.new4net.sso.core.entity.Module;
import com.new4net.sso.core.entity.User;
import com.new4net.sso.core.repo.AuthorityReposity;
import com.new4net.sso.core.repo.UserReposity;
import com.new4net.sso.core.service.AuthorityService;
import com.new4net.sso.core.service.impl.UserService;
import com.new4net.util.AjaxMsg;
import com.new4net.util.Page;
import com.new4net.util.PageConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);
    @Autowired
    private UserReposity userReposity;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthorityService authorityService;


    @RequestMapping("/regByAccount")
    @ResponseBody
    public AjaxMsg regByAccount(HttpServletRequest req, @RequestParam("username") String username
            , @RequestParam("password") String password,
                                @RequestParam("email") String email, @RequestParam("vCode") String vCode) {
        try {
            if (StringUtils.isEmpty(email)) {
                return new AjaxMsg("0", "Email为空");
            }
            if (StringUtils.isEmpty(username)) {
                return new AjaxMsg("-1", "账户为空");
            }
            String kaptchaExpected = (String) req.getSession()
                    .getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
            if (kaptchaExpected == null || vCode == null || !kaptchaExpected.toUpperCase().equals(vCode.toUpperCase())) {
                return new AjaxMsg("-2", "验证码错误!");
            }
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);
            user.setEnable(true);
            Authority authority = authorityService.findById("ROLE_USER");
            Set<Authority> set = new HashSet<>();
            set.add(authority);
            user.setAuthorities(set);
            userReposity.save(user);

            return new AjaxMsg("1", "注册成功");
        } catch (Exception e) {
            logger.error("账号注册失败", e);
        }
        return new AjaxMsg("-3", "其他错误!");
    }

    @Autowired
    private UserService userService;

    @RequestMapping("/listUsers")
    @PreAuthorize("hasRole('ROLE_SYSTEMADMIN')")
    @ResponseBody
    public Page<UserInfo> listUsers(@RequestBody Map<String, Object> params) {
        int pageNo = (int) params.get("pageNo");
        int pageSize = (int) params.get("pageSize");
        String username = (String) params.get("username");
        Object en = params.get("enable");
        Boolean enable = null;
        if (!(en instanceof Boolean)) {
            enable = Boolean.parseBoolean(String.valueOf(en));
        } else {
            enable = (Boolean) en;
        }
        String mobile = (String) params.get("mobile");
        String email = (String) params.get("email");
        Map<String, Object> ps = new HashMap<>();

        if (!StringUtils.isEmpty(username)) {
            ps.put("username||like", username);

        }
        if (!StringUtils.isEmpty(mobile)) {
            ps.put("mobile||like", mobile);

        }
        if (!StringUtils.isEmpty(email)) {
            ps.put("email||like", email);

        }
        if (enable != null) {
            ps.put("enable", enable);

        }

        Page<User> userPage = userService.queryPage(pageNo, pageSize, ps);

        return new PageConverter<User, UserInfo>().convert(userPage, (user) -> {

            return UserInfo.builder().enable(user.isEnable())
                    .username(user.getUsername())
                    .enable(user.isEnable())
                    .mobile(user.getMobile())
                    .email(user.getEmail())
                    .authorities(user.getAuthorities() == null ? null : user.getAuthorities().stream().map(authority -> {
                        return authority.getAuth();
                    }).collect(Collectors.toSet()))
                    .credentialsNonExpired(user.isCredentialsNonExpired())
                    .accountNonLocked(user.isAccountNonLocked())
                    .accountNonExpired(user.isAccountNonExpired())
                    .build();
        });

    }

    @Autowired
    private AuthorityReposity authorityReposity;

    @RequestMapping("/modifyUser")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_SYSTEMADMIN')")
    public AjaxMsg modifyUser(@RequestBody UserInfo user) {
        User user1 = userReposity.findByUsername(user.getUsername());
        if(user.getAuthorities()!=null){

            List<Authority> authorityList = authorityReposity.findAllById(user.getAuthorities().stream().map(auth -> {
                return auth.getAuthority();
            }).collect(Collectors.toSet()));
            user1.setAuthorities(new HashSet<>(authorityList));
        }
        user1.setEnable(user.isEnable());
        user1.setCredentialsNonExpired(user.isCredentialsNonExpired());
        user1.setAccountNonLocked(user.isAccountNonLocked());
        user1.setAccountNonExpired(user.isAccountNonExpired());
        user1.setEmail(user.getEmail());
        user1.setMobile(user.getMobile());
        userReposity.save(user1);
        return new AjaxMsg("1", "执行成功");
    }

}
