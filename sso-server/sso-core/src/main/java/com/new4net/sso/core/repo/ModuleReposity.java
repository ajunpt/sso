package com.new4net.sso.core.repo;

import com.new4net.sso.core.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleReposity extends JpaRepository<Module,String> {
    public Module findByModuleName(String userName);
}
