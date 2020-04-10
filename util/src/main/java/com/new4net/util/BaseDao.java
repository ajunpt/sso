package com.new4net.util;

import org.hibernate.Session;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * dao层基类，实现增删改查
 *
 * @param <T>
 */
public interface BaseDao<T> {
    Session getSession();

    //添加一个对象
    void save(T entity);

    //更新一个对象，所有属性
    void update(T entity);

    void delete(T entity);

    void persist(T entity);

    void delete(Serializable id);

    //根据id查找一个对象
    T findById(Serializable id);

    //根据HQL返回对象List
    List<T> findByHQL(String hql, Object... params);

    //分页查询
    public Page<T> queryPage(String hql, int pageNo, int pageSize, Map<String, Object> params);

    public Page<T> queryPage(int pageNo, int pageSize, Map<String, Object> params);

    Object doWorkInTransaction(Work work);
    public List<T> findByHQL(String hql, Map<String, Object> params);
    public List<T> findByParams(Map<String, Object> params);
}
