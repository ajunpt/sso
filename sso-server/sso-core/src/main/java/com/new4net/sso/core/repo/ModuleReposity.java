package com.new4net.sso.core.repo;

import com.new4net.sso.core.entity.Module;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;


public interface ModuleReposity<QueryHints> extends JpaRepository<Module,String> {
    public Module findByModuleName(String userName);
}
