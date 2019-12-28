package com.new4net.sso.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Configuration
public class ApplicationContextUtils implements BeanFactoryAware {

    private static BeanFactory beanFactory;


    public static Object getBean(String beanName) {
        return beanFactory.getBean(beanName);
    }

    public static <T> T getBean(Class<T> t) {
        return beanFactory.getBean(t);
    }



    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory=beanFactory;

    }
}
