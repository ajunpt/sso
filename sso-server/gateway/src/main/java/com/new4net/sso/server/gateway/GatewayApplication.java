package com.new4net.sso.server.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableEurekaClient
@EnableZuulProxy
public class GatewayApplication {
    @Value("${cros.allowOrigin}")

    private String allowOrigin;

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
    @Autowired
    private RedisTemplate redisTemplate;
    @Bean
    public ServletRegistrationBean myServlet() {
        MyKaptchaServlet servlet = new MyKaptchaServlet(redisTemplate);
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
    public FilterRegistrationBean testFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new VCodeFilter(redisTemplate));
        registration.addUrlPatterns("/api/login","/api/user/regByAccount");
        registration.setName("vCodeFilter");
        registration.setOrder(Integer.MIN_VALUE+1);
        return registration;
    }
    @Bean
    public FilterRegistrationBean test1FilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new CrosFilter(allowOrigin));
        registration.addUrlPatterns("/*");
        registration.setName("crosFilter");
        registration.setOrder(Integer.MIN_VALUE);
        return registration;
    }
}
