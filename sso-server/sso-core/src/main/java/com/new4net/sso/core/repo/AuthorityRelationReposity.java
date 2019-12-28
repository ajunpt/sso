package com.new4net.sso.core.repo;

import com.new4net.sso.core.entity.Authority;
import com.new4net.sso.core.entity.AuthorityRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorityRelationReposity extends JpaRepository<AuthorityRelation,String> {
    void deleteBySubAuthCode(String authorityCode);

    List<AuthorityRelation> findBySuperAuthCode(String authorityCode);

    List<AuthorityRelation> findBySubAuthCode(String authorityCode);
}
