package com.new4net.sso.core.repo;

import com.new4net.sso.core.entity.Module;
import com.new4net.sso.core.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface AuthorityReposity extends JpaRepository<Authority,String> {
    public Set<Authority> findByModule(Module module);

    void deleteByAuthorityCode(String authority);

    void deleteByModule(Module m);
}
