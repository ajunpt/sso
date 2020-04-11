package com.new4net.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

public class CustomTransactionManager {

    public Session getSession() {
        return ((SessionFactory) CustomApplicationContext.getContext().getAttr(Constants.SESSIONFACTORY)).getCurrentSession();
    }

    public Object doWorkInTransaction(Work work) {
        TransactionTemplate transactionTemplate = (TransactionTemplate) CustomApplicationContext.getContext().getAttr(Constants.TRANSACTIONTEMPLATE);
        if (transactionTemplate != null) {
            return transactionTemplate.execute(new TransactionCallback() {
                public Object doInTransaction(TransactionStatus status) {
                    Object result;
                    try {
                        return work.run(getSession());
                    } catch (Exception e) {
                        status.setRollbackOnly();
                    }
                    return null;
                }
            });
        } else {
            Transaction tx = null;
            int isolation = -1;
            Connection connection = null;
            Session session = getSession();
            if (session.getTransaction() != null &&
                    session.getTransaction().isActive()
            ) {
                return work.run(session);
            } else {
                try {
                    tx = session.beginTransaction();
                    Method methodToUse = session.getClass().getMethod("connection");
                    connection = (Connection) methodToUse.invoke(session);
                    isolation = connection.getTransactionIsolation();
                    connection.setTransactionIsolation(work.getIsolationLevel());

                    Object o = work.run(session);
                    tx.commit();
                    return o;
                } catch (Exception ex) {
                    ex.printStackTrace();

                    if (tx != null) {
                        tx.rollback();
                    }
                    throw new RuntimeException(ex);
                } finally {
                    try {
                        if (connection != null && isolation != -1 && !connection.isClosed()) {
                            connection.setTransactionIsolation(isolation);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            }
        }

    }

}
