package com.new4net.sso.core.service.impl;

import com.new4net.sso.core.dao.BaseDao;
import com.new4net.sso.core.service.BaseService;
import com.new4net.util.CustomTransactionManager;
import com.new4net.util.Page;
import com.new4net.util.Work;
import org.hibernate.Session;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BaseServiceImpl<T> implements BaseService<T> {

    protected BaseDao<T> baseDao;

    @Override
    public void save(T entity) {

        baseDao.save(entity);
    }

    @Override
    public List<T> findByHQL(String hql, Map<String, Object> params) {
        return baseDao.findByHQL(hql,params);
    }

    @Override
    public List<T> findByParams(Map<String, Object> params) {
        return baseDao.findByParams(params);
    }



    @Override
    public void update(T entity) {
        baseDao.update(entity);
    }


    @Override
    public void delete(T entity) {
        baseDao.delete(entity);
    }

    @Override
    public void delete(Serializable id) {
        baseDao.delete(id);
    }

    @Override
    public T findById(Serializable id) {
        return baseDao.findById(id);
    }

    @Override
    public List<T> findByHQL(String hql, Object... params) {
        return baseDao.findByHQL(hql,params);
    }

    @Override
    public Page<T> queryPage(String hql, int pageNo, int pageSize, Map<String,Object> params) {
        return baseDao.queryPage(hql,pageNo,pageSize,  params);
    }

    @Override
    public Page<T> queryPage(int pageNo, int pageSize, Map<String, Object> params) {
        return baseDao.queryPage(pageNo,pageSize,params);
    }

    protected void setBaseDao(BaseDao<T> baseDao) {
        this.baseDao=baseDao;
    }
}
