package com.new4net.util;

import org.hibernate.Session;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BaseServiceImpl<T> implements BaseService<T> {
    private CustomTransactionManager manager;

    protected BaseDao<T> baseDao;
    public BaseServiceImpl(){
        manager = new CustomTransactionManager();
    }

    @Override
    public void save(T entity) {

        baseDao.save(entity);
    }
    @Override
    public Object doWorkInTransaction(Work work){
        return manager.doWorkInTransaction(work);
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
    public Session getSession() {
        return baseDao.getSession();
    }

    @Override
    public void update(T entity) {
        baseDao.update(entity);
    }
    @Override
    public void persist(T entity){
        baseDao.persist(entity);
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
}
