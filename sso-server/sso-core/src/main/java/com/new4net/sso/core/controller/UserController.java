package com.new4net.sso.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.new4net.sso.api.dto.UserInfo;
import com.new4net.sso.core.RabbitConfig;
import com.new4net.sso.core.entity.Authority;
import com.new4net.sso.core.entity.User;
import com.new4net.sso.core.repo.AuthorityReposity;
import com.new4net.sso.core.repo.UserReposity;
import com.new4net.sso.core.service.impl.UserService;
import com.new4net.util.AjaxMsg;
import com.new4net.util.Page;
import com.new4net.util.PageConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    private RabbitTemplate rabbitTemplate;


    @RequestMapping("/regByAccount")
    @ResponseBody
    @HystrixCommand(fallbackMethod = "asynRegByAccount")
    public AjaxMsg regByAccount(HttpServletRequest req, @RequestParam("username") String username
            , @RequestParam("password") String password,
                                @RequestParam("email") String email) {
        if (StringUtils.isEmpty(email)) {
            return new AjaxMsg("0", "Email为空");
        }
        if (StringUtils.isEmpty(username)) {
            return new AjaxMsg("-1", "账户为空");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnable(true);
        Authority authority = authorityReposity.getOne("ROLE_USER");
        Set<Authority> set = new HashSet<>();
        set.add(authority);
        user.setAuthorities(set);
        userReposity.save(user);

        return new AjaxMsg("1", "注册成功");


    }

    public AjaxMsg asynRegByAccount(HttpServletRequest req, @RequestParam("username") String username
            , @RequestParam("password") String password,
                                    @RequestParam("email") String email) {
        try {
            if (StringUtils.isEmpty(email)) {
                return new AjaxMsg("0", "Email为空");
            }
            if (StringUtils.isEmpty(username)) {
                return new AjaxMsg("-1", "账户为空");
            }
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);
            user.setEnable(true);
            Authority authority = authorityReposity.findById("ROLE_USER").get();
            Set<Authority> set = new HashSet<>();
            set.add(authority);
            user.setAuthorities(set);
            String message = JSONObject.toJSONString(user);

            rabbitTemplate.convertAndSend(RabbitConfig.USER_EXCHANGE, RabbitConfig.ROUTINGKEY, message);

            return new AjaxMsg("2", "注册申请已提交,正在处理中.....");
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
        int pageNo = Integer.parseInt(String.valueOf(params.get("pageNo")));
        int pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
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
                        return authority.buildAuth();
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
        if (user.getAuthorities() != null) {

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
