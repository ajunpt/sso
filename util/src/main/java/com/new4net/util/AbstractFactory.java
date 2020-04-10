package com.new4net.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.transaction.support.TransactionTemplate;


public abstract class AbstractFactory {
    private Logger logger = LogManager.getLogger(this.getClass());
    private CustomApplicationContext context;
    private SessionFactory sessionFactory;
    private TransactionTemplate transactionTemplate;

    public TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        if (this.transactionTemplate == null) {
            this.transactionTemplate = transactionTemplate;
            CustomApplicationContext.getContext().addAttr(Constants.TRANSACTIONTEMPLATE, transactionTemplate);
        }

    }

    public AbstractFactory() {
        this.context = CustomApplicationContext.getContext();
        try {
            sessionFactory = new Configuration()
                    .configure().buildSessionFactory();
            CustomApplicationContext.getContext().addAttr(Constants.SESSIONFACTORY, sessionFactory);
        } catch (Exception e) {
            logger.info("no hibernate.cfg.xml");
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {


        if (this.sessionFactory == null) {
            this.sessionFactory = sessionFactory;
            CustomApplicationContext.getContext().addAttr(Constants.SESSIONFACTORY, sessionFactory);
        }
    }

    public CustomApplicationContext getContext() {
        return context;
    }
}
