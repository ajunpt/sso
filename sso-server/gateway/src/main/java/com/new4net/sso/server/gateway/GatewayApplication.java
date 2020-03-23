package com.new4net.sso.server.gateway;

import com.google.code.kaptcha.servlet.KaptchaServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableEurekaClient
@EnableZuulProxy
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
    @Bean
    public ServletRegistrationBean myServlet() {
        KaptchaServlet servlet = new KaptchaServlet();
        ServletRegistrationBean bean = new ServletRegistrationBean(servlet, "/kaptcha.jpg");
        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("kaptcha.border", "no");
        initParameters.put("kaptcha.image.width", "112");
        initParameters.put("kaptcha.image.height", "50");
        initParameters.put("kaptcha.textproducer.char.length", "4");
        initParameters.put("kaptcha.textproducer.font.color", "red");
        bean.setInitParameters(initParameters);
        return bean;
    }
    @Bean
    public VCodeFilter vCodeFilter(){
        return new VCodeFilter();
    }
}
