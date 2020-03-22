package com.new4net.sso.core.dao;

import com.new4net.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hibernate.jpa.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Transactional
public abstract class BaseDaoImpl<T> implements BaseDao<T> {

    private Logger logger = LogManager.getLogger(this.getClass());
    private Class<T> clazz;
    private String clazzName;

    @Autowired
    private EntityManager entityManager;
    EntityManager entityManager(){
       return entityManager;

    }

    public BaseDaoImpl() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        clazz = (Class<T>) type.getActualTypeArguments()[0];
        clazzName = clazz.getName();
    }



    @Override
    public void save(Object entity) {
        
        entityManager.persist(entity);
       


    }

    @Override
    public void update(Object entity) {
       
        entityManager.persist(entity);
       

    }


    @Override
    public void delete(T obj) {
        
        entityManager.remove(obj);
       
    }

    @Override
    public void delete(Serializable id) {
        T obj = findById(id);
        delete(obj);
    }


    @Override
    @SuppressWarnings("unchecked")
    public T findById(Serializable id) {
        EntityManager entityManager = entityManager();
        try {
            return entityManager.find(clazz, id);
        }finally {
           
        }


    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> findByHQL(String hql, Object... params) {
        EntityManager entityManager = entityManager();
        try {
            Query query = entityManager.createQuery(hql);
            query.setHint(QueryHints.HINT_CACHEABLE,true);
            for (int i = 0; params != null && i < params.length; i++) {
                query.setParameter(i, params[i]);
            }

            return query.getResultList();
        }finally {
           
        }


    }

    @SuppressWarnings("unchecked")
    public Page<T> queryPage(String hql, int pageNo, int pageSize, Map<String, Object> params) {

        EntityManager entityManager = entityManager();
        try {
            Query query = entityManager.createQuery(hql);
            TypedQuery<Long> query1 = entityManager.createQuery("select Count(*) " + hql, Long.class);
            query.setHint(QueryHints.HINT_CACHEABLE,true);
            query1.setHint(QueryHints.HINT_CACHEABLE,true);
            for (Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Object> entry = it.next();
                query.setParameter(entry.getKey(), entry.getValue());
                query1.setParameter(entry.getKey(), entry.getValue());
            }

            List<T> list = query.setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize).getResultList();
            Long count = query1.getSingleResult();
            Page<T> page = new Page<T>(pageNo, pageSize, count, list);
            return page;
        }finally {
           
        }


    }

    public List<T> findByHQL(String hql, Map<String, Object> params) {
        EntityManager entityManager = entityManager();
        try {
            Query query = entityManager.createQuery(hql);
            query.setHint(QueryHints.HINT_CACHEABLE,true);
            for (Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Object> entry = it.next();
                query.setParameter(entry.getKey(), entry.getValue());

            }
            List<T> list = query.getResultList();
            return list;
        }finally {
           
        }

    }
    public List<T> findByParams(Map<String, Object> params) {
        EntityManager entityManager = entityManager();
        try {
            String hql1 = "From " + clazzName + " p";

            String idFieldName = (String) params.remove("idFieldName");
            if (idFieldName == null || "".equals(idFieldName.trim())) {
                idFieldName = "id";
            }
            if (params != null && !params.isEmpty()) {
                hql1 += " Where ";
            }

            for (Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Object> entry = it.next();
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key != null && value != null) {
                    key = key.trim();
                    String[] ss = key.split("\\|\\|");
                    if (ss.length == 1) {
                        String fieldName = ss[0];

                        hql1 += " p." + fieldName + " =:" + fieldName + " and";
                    } else if (ss.length == 2) {
                        String fieldName = ss[0];
                        String exp = ss[1];
                        if ("like".equals(exp)) {
                            String s = " concat('%',:" + fieldName + ",'%') ";
                            hql1 += " p." + fieldName + " " + exp + " " + s + " and";
                        } else if ("in".equals(exp) || "IN".equals(exp)) {
                            String s = " (:" + fieldName + ") ";
                            hql1 += " p." + fieldName + " " + exp + " " + s + " and";
                        } else {

                            hql1 += " p." + fieldName + " " + exp + " :" + fieldName + " and";
                        }

                    } else if (ss.length == 3) {
                        String fieldName = ss[0];
                        String exp = ss[1];
                        String paramName = ss[2];
                        if ("like".equals(exp)) {
                            String s = " concat('%',:" + paramName + ",'%') ";
                            hql1 += " p." + fieldName + " " + exp + " " + s + " and";
                        } else if ("in".equals(exp) || "IN".equals(exp)) {
                            String s = " (:" + paramName + ") ";
                            hql1 += " p." + fieldName + " " + exp + " " + s + " and";
                        } else {
                            hql1 += " p." + fieldName + " " + exp + " :" + paramName + " and";
                        }

                    }
                }
            }
            if (hql1.endsWith("and")) {
                hql1 = hql1.substring(0, hql1.lastIndexOf("and"));
            }


            Query query1 = entityManager.createQuery(hql1);
            query1.setHint(QueryHints.HINT_CACHEABLE,true);
            for (Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Object> entry = it.next();
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key != null && value != null) {
                    key = key.trim();
                    String[] ss = key.split("\\|\\|");
                    if (ss.length == 1) {
                        String fieldName = ss[0];
                        if (value instanceof Collection) {
                            query1.setParameter(fieldName, (Collection) value);
                        } else {
                            query1.setParameter(fieldName, value);
                        }

                    } else if (ss.length == 2) {
                        String fieldName = ss[0];
                        String exp = ss[1];

                        if (value instanceof Collection) {
                            query1.setParameter(fieldName, (Collection) value);
                        } else {
                            query1.setParameter(fieldName, value);
                        }
                    } else if (ss.length == 3) {
                        String fieldName = ss[0];
                        String exp = ss[1];
                        String paramName = ss[2];
                        if (value instanceof Collection) {
                            query1.setParameter(paramName, (Collection) value);
                        } else {
                            query1.setParameter(paramName, value);
                        }
                    }


                }
            }

            List<T> list = query1.getResultList();
            return list;
        }finally {
           
        }


    }
    public Page<T> queryPage(int pageNo, int pageSize, Map<String, Object> params) {
        EntityManager entityManager = entityManager();
        try {
            String hql1 = "From " + clazzName + " p";
            String idFieldName = (String) params.remove("idFieldName");
            if (idFieldName == null || "".equals(idFieldName.trim())) {
                idFieldName = "id";
            }
            String hql2 = "select Count(distinct p." + idFieldName + ") From " + clazzName + " p ";
            if (params != null && !params.isEmpty()) {
                hql1 += " Where ";
                hql2 += " Where ";
            }

            for (Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Object> entry = it.next();
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key != null && value != null) {
                    key = key.trim();
                    String[] ss = key.split("\\|\\|");
                    if (ss.length == 1) {
                        String fieldName = ss[0];

                        hql1 += " p." + fieldName + " =:" + fieldName + " and";
                        hql2 += " p." + fieldName + " =:" + fieldName + " and";
                    } else if (ss.length == 2) {
                        String fieldName = ss[0];
                        String exp = ss[1];
                        if ("like".equals(exp)) {
                            String s = " concat('%',:" + fieldName + ",'%') ";
                            hql1 += " p." + fieldName + " " + exp + " " + s + " and";
                            hql2 += " p." + fieldName + " " + exp + " " + s + " and";
                        } else if ("in".equals(exp) || "IN".equals(exp)) {
                            String s = " (:" + fieldName + ") ";
                            hql1 += " p." + fieldName + " " + exp + " " + s + " and";
                            hql2 += " p." + fieldName + " " + exp + " " + s + " and";
                        } else {

                            hql1 += " p." + fieldName + " " + exp + " :" + fieldName + " and";
                            hql2 += " p." + fieldName + " " + exp + " :" + fieldName + " and";
                        }

                    } else if (ss.length == 3) {
                        String fieldName = ss[0];
                        String exp = ss[1];
                        String paramName = ss[2];
                        if ("like".equals(exp)) {
                            String s = " concat('%',:" + paramName + ",'%') ";
                            hql1 += " p." + fieldName + " " + exp + " " + s + " and";
                            hql2 += " p." + fieldName + " " + exp + " " + s + " and";
                        } else if ("in".equals(exp) || "IN".equals(exp)) {
                            String s = " (:" + paramName + ") ";
                            hql1 += " p." + fieldName + " " + exp + " " + s + " and";
                            hql2 += " p." + fieldName + " " + exp + " " + s + " and";
                        } else {
                            hql1 += " p." + fieldName + " " + exp + " :" + paramName + " and";
                            hql2 += " p." + fieldName + " " + exp + " :" + paramName + " and";
                        }

                    }
                }
            }
            if (hql1.endsWith("and")) {
                hql1 = hql1.substring(0, hql1.lastIndexOf("and"));
            }
            if (hql2.endsWith("and")) {
                hql2 = hql2.substring(0, hql2.lastIndexOf("and"));
            }

            Query query1 = entityManager.createQuery(hql1);
            query1.setHint(QueryHints.HINT_CACHEABLE,true);
            TypedQuery<Long> query2 = entityManager.createQuery(hql2, Long.class);
            query2.setHint(QueryHints.HINT_CACHEABLE,true);
            for (Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Object> entry = it.next();
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key != null && value != null) {
                    key = key.trim();
                    String[] ss = key.split("\\|\\|");
                    if (ss.length == 1) {
                        String fieldName = ss[0];
                        if (value instanceof Collection) {
                            query1.setParameter(fieldName, (Collection) value);
                            query2.setParameter(fieldName, (Collection) value);
                        } else {
                            query1.setParameter(fieldName, value);
                            query2.setParameter(fieldName, value);
                        }

                    } else if (ss.length == 2) {
                        String fieldName = ss[0];
                        String exp = ss[1];

                        if (value instanceof Collection) {
                            query1.setParameter(fieldName, (Collection) value);
                            query2.setParameter(fieldName, (Collection) value);
                        } else {
                            query1.setParameter(fieldName, value);
                            query2.setParameter(fieldName, value);
                        }
                    } else if (ss.length == 3) {
                        String fieldName = ss[0];
                        String exp = ss[1];
                        String paramName = ss[2];
                        if (value instanceof Collection) {
                            query1.setParameter(paramName, (Collection) value);
                            query2.setParameter(paramName, (Collection) value);
                        } else {
                            query1.setParameter(paramName, value);
                            query2.setParameter(paramName, value);
                        }
                    }


                }
            }

            Page<T> page = null;
            if (pageNo > 0) {
                List<T> list = query1.setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize).getResultList();
                Long count = query2.getSingleResult();
                page = new Page<T>(pageNo, pageSize, count, list);
                return page;
            } else {
                List<T> list = query1.getResultList();
                if (list != null) {
                    page = new Page<T>(pageNo, pageSize, list.size(), list);
                    return page;
                } else {
                    return new Page<>();
                }

            }
        }finally {
           
        }


    }


}
