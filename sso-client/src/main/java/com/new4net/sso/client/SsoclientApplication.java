package com.new4net.sso.client;

import com.new4net.jwt.client.configuration.EnableJwtClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;


@EnableDiscoveryClient
@EnableJwtClient
@EnableFeignClients(basePackages = "com.new4net.sso")
@SpringBootApplication
@ComponentScan(basePackages = "com.new4net")
public class SsoclientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SsoclientApplication.class, args);
	}
	@Bean
    @LoadBalanced
    RestTemplate getRestTemplate(){
	    return new RestTemplate();
    }
}
