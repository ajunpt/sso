package com.new4net.sso.api.dto;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
@Setter
@Getter
@ToString

@Builder
public class UserInfo implements UserDetails {
    private String username;
    private String password;
    private String email;
    private String mobile;
    private boolean accountNonExpired;//账号是否未过期
    private boolean accountNonLocked; //账号是否未锁定
    private boolean credentialsNonExpired;//密码是否未过期
    private boolean enable; //是否激活
    private Set<Auth> authorities;

    public UserInfo(String username, String password, String email, String mobile, boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired, boolean enable, Set<Auth> authorities) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.mobile = mobile;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enable = enable;
        this.authorities = authorities;
    }

    public UserInfo() {
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }
}
