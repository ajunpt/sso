package com.new4net.sso.core.service.impl;

import com.new4net.sso.core.dao.BaseDao;
import com.new4net.sso.core.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Component
@Transactional
public class UserService extends BaseServiceImpl<User> {
    public UserService(){
    }
    @Resource(name="userDao")
    public void setBaseDao(BaseDao<User> baseDao) {
        super.setBaseDao(baseDao);
    }
}
