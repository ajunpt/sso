package com.new4net.sso.core;

import com.new4net.jwt.server.configuration.EnableJwtServer;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManagerFactory;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJwtServer
//@EnableEurekaServer
@EnableCaching
public class SsoCoreApplication {
	public static void main(String[] args) {
		SpringApplication.run(SsoCoreApplication.class, args);

	}

}
