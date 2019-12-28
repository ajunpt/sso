package com.new4net.sso.core.repo;

import com.new4net.sso.core.entity.Module;
import com.new4net.sso.core.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface RoleReposity extends JpaRepository<Role,String> {
    public Set<Role> findByModule(Module module);

    void deleteByRoleCode(String authority);

    void deleteByModule(Module m);
}
