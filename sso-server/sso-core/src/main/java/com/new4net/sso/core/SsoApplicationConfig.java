package com.new4net.sso.core;

import com.google.code.kaptcha.servlet.KaptchaServlet;
import com.new4net.util.Constants;
import com.new4net.util.CustomApplicationContext;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

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
