package com.new4net.sso.core.service;

import com.new4net.sso.core.entity.Authority;

import java.util.List;

public interface AuthorityService extends BaseService<Authority> {
    public List<Authority> getSubAuthorities( String moduleName,Authority ...superAuthority);
}
