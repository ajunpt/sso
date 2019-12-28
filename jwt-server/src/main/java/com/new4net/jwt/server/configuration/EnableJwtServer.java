package com.new4net.jwt.server.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(WebSecurityConfig.class)
@ComponentScan("com.new4net.jwt.server")
@Documented
public @interface EnableJwtServer {

}
