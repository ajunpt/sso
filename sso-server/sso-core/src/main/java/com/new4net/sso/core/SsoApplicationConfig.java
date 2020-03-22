package com.new4net.sso.core;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration

public class SsoApplicationConfig implements InitializingBean {


    @Bean(name = "transactionManager")
    public PlatformTransactionManager platformTransactionManager(EntityManagerFactory entityManagerFactory, DataSource dataSource) {
        JpaTransactionManager platformTransactionManager = new JpaTransactionManager();
        platformTransactionManager.setDataSource(dataSource);
        platformTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return platformTransactionManager;
    }
//
//    @Bean(name = "transactionTemplate")
//    TransactionTemplate transactionTemplate(EntityManagerFactory entityManagerFactory, DataSource dataSource) {
//        TransactionTemplate transactionTemplate = new TransactionTemplate();
//        HibernateTransactionManager platformTransactionManager = new HibernateTransactionManager();
//        platformTransactionManager.setDataSource(dataSource);
//        platformTransactionManager.setSessionFactory((SessionFactory)entityManagerFactory.unwrap(SessionFactory.class));
//        transactionTemplate.setTransactionManager(platformTransactionManager);
//        return transactionTemplate;
//    }



    @Override
    public void afterPropertiesSet() throws Exception {
//        SessionFactory sessionFactory = (SessionFactory)entityManagerFactory.unwrap(SessionFactory.class);
//        CustomApplicationContext.getContext().addAttr(Constants.TRANSACTIONTEMPLATE,transactionTemplate);
//        CustomApplicationContext.getContext().addAttr(Constants.SESSIONFACTORY,sessionFactory);

    }
}
