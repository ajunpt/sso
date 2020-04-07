package com.new4net.sso.core.repo;

import com.new4net.sso.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface UserReposity extends JpaRepository<User,String> {
    public User findByUsername(String userName);

    User findByEmail(String email);
}
