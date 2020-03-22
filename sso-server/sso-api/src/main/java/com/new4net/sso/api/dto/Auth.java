package com.new4net.sso.api.dto;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Setter
@Getter
@ToString
@EqualsAndHashCode(of = {"authority"})

@Builder
public class Auth implements GrantedAuthority {
    private String moduleName;
    public Auth() {
    }

    public Auth(String moduleName,String authority, String authorityName, String remark, Set<AuthorityRelationInfo> authorityRelationInfos) {
        this.authority = authority;
        this.authorityName = authorityName;
        this.remark = remark;
        this.authorityRelationInfos = authorityRelationInfos;
    }

    protected String authority;
    protected String authorityName;
    protected String remark;
    protected Set<AuthorityRelationInfo> authorityRelationInfos;
    @Override
    public String getAuthority() {
        return authority;
    }
}
