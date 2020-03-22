package com.new4net.sso.core;

import com.new4net.jwt.server.configuration.EnableJwtServer;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJwtServer
//@EnableEurekaServer
@EnableCaching
@EnableTransactionManagement(proxyTargetClass = true)
@EnableConfigurationProperties
@EnableCircuitBreaker
public class SsoCoreApplication {
	public static void main(String[] args) {
		SpringApplication.run(SsoCoreApplication.class, args);

	}

}
