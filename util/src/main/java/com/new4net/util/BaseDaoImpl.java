package com.new4net.util;

import antlr.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.*;


public class BaseDaoImpl<T> implements BaseDao<T> {

    private Logger logger = LogManager.getLogger(this.getClass());
    private Class<T> clazz;
    private String clazzName;
    private CustomTransactionManager manager;

    public BaseDaoImpl() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        clazz = (Class<T>) type.getActualTypeArguments()[0];
        clazzName = clazz.getName();
        manager = new CustomTransactionManager();
    }

    private Session getCurrentSession() {
        return ((SessionFactory) CustomApplicationContext.getContext().getAttr(Constants.SESSIONFACTORY)).getCurrentSession();
    }


    public Session getSession() {
        return getCurrentSession();
    }

    @Override
    public void save(Object entity) {

        doWorkInTransaction(new Work() {
            @Override
            public Object run(Session session) {
                session.save(entity);
                return null;
            }
        });


    }

    @Override
    public void persist(Object entity) {

        doWorkInTransaction(new Work() {
            @Override
            public Object run(Session session) {
                session.persist(entity);
                return null;
            }
        });


    }

    @Override
    public void update(Object entity) {
        doWorkInTransaction(new Work() {
            @Override
            public Object run(Session session) {
                session.update(entity);
                return null;
            }
        });


    }


    @Override
    public void delete(T obj) {
        doWorkInTransaction(new Work() {
            @Override
            public Object run(Session session) {
                session.delete(obj);
                return null;
            }
        });
    }

    @Override
    public void delete(Serializable id) {
        T obj = findById(id);
        delete(obj);
    }


    @Override
    @SuppressWarnings("unchecked")
    public T findById(Serializable id) {
        return (T) doWorkInTransaction(new Work() {
            @Override
            public Object run(Session session) {
                return session.get(clazz, id);
            }
        });

    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> findByHQL(String hql, Object... params) {
        return (List<T>) doWorkInTransaction(new Work() {
            @Override
            public Object run(Session session) {
                Query query = session.createQuery(hql);
                for (int i = 0; params != null && i < params.length; i++) {
                    query.setParameter(i, params[i]);
                }
                query.setComment("HQL查询：" + hql);

                return query.list();
            }
        });

    }

    @SuppressWarnings("unchecked")
    public Page<T> queryPage(String hql, int pageNo, int pageSize, Map<String, Object> params) {
        return (Page<T>) doWorkInTransaction(new Work() {
            @Override
            public Object run(Session session) {
                Query query = session.createQuery(hql);
                Query<Long> query1 = session.createQuery("select Count(*) " + hql, Long.class);
                for (Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<String, Object> entry = it.next();
                    query.setParameter(entry.getKey(), entry.getValue());
                    query1.setParameter(entry.getKey(), entry.getValue());
                }
                query.setComment("HQL查询：" + hql);
                query1.setComment("HQL查询：" + "select Count(*) " + hql);
                List<T> list = query.setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize).list();
                Long count = query1.uniqueResult();
                Page<T> page = new Page<T>(pageNo, pageSize, count, list);
                return page;
            }
        });

    }

    public List<T> findByHQL(String hql, Map<String, Object> params) {
        return (List<T>) doWorkInTransaction(new Work() {
            @Override
            public Object run(Session session) {
                Query query = session.createQuery(hql);
                for (Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<String, Object> entry = it.next();
                    query.setParameter(entry.getKey(), entry.getValue());
                }
                query.setComment("HQL查询：" + hql);
                List<T> list = query.list();
                return list;
            }
        });

    }
    public List<T> findByParams(Map<String, Object> params) {
        return (List<T>) doWorkInTransaction(new Work() {
            @Override
            public Object run(Session session) {
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


                Query query1 = session.createQuery(hql1);
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
                                query1.setParameterList(fieldName, (Collection) value);
                            } else {
                                query1.setParameter(fieldName, value);
                            }

                        } else if (ss.length == 2) {
                            String fieldName = ss[0];
                            String exp = ss[1];

                            if (value instanceof Collection) {
                                query1.setParameterList(fieldName, (Collection) value);
                            } else {
                                query1.setParameter(fieldName, value);
                            }
                        } else if (ss.length == 3) {
                            String fieldName = ss[0];
                            String exp = ss[1];
                            String paramName = ss[2];
                            if (value instanceof Collection) {
                                query1.setParameterList(paramName, (Collection) value);
                            } else {
                                query1.setParameter(paramName, value);
                            }
                        }


                    }
                }

                query1.setComment("HQL查询：" + hql1);
                List<T> list = query1.list();
                return list;
            }
        });

    }
    public Page<T> queryPage(int pageNo, int pageSize, Map<String, Object> params) {
        return (Page<T>) doWorkInTransaction(new Work() {
            @Override
            public Object run(Session session) {
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

                Query query1 = session.createQuery(hql1);
                Query<Long> query2 = session.createQuery(hql2, Long.class);
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
                                query1.setParameterList(fieldName, (Collection) value);
                                query2.setParameterList(fieldName, (Collection) value);
                            } else {
                                query1.setParameter(fieldName, value);
                                query2.setParameter(fieldName, value);
                            }

                        } else if (ss.length == 2) {
                            String fieldName = ss[0];
                            String exp = ss[1];

                            if (value instanceof Collection) {
                                query1.setParameterList(fieldName, (Collection) value);
                                query2.setParameterList(fieldName, (Collection) value);
                            } else {
                                query1.setParameter(fieldName, value);
                                query2.setParameter(fieldName, value);
                            }
                        } else if (ss.length == 3) {
                            String fieldName = ss[0];
                            String exp = ss[1];
                            String paramName = ss[2];
                            if (value instanceof Collection) {
                                query1.setParameterList(paramName, (Collection) value);
                                query2.setParameterList(paramName, (Collection) value);
                            } else {
                                query1.setParameter(paramName, value);
                                query2.setParameter(paramName, value);
                            }
                        }


                    }
                }

                query1.setComment("HQL查询：" + hql1);
                query2.setComment("HQL查询：" + hql2);
                Page<T> page = null;
                if (pageNo > 0) {
                    List<T> list = query1.setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize).list();
                    Long count = query2.uniqueResult();
                    page = new Page<T>(pageNo, pageSize, count, list);
                    return page;
                } else {
                    List<T> list = query1.list();
                    if (list != null) {
                        page = new Page<T>(pageNo, pageSize, list.size(), list);
                        return page;
                    } else {
                        return new Page<>();
                    }

                }

            }
        });

    }

    @Override
    public Object doWorkInTransaction(Work work) {
        return manager.doWorkInTransaction(work);
    }

}
