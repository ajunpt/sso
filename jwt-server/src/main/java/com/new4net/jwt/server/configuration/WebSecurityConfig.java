package com.new4net.jwt.server.configuration;


import com.google.code.kaptcha.servlet.KaptchaServlet;
import com.new4net.jwt.client.configuration.Constants;
import com.new4net.jwt.client.configuration.JwtClientProperties;
import com.new4net.jwt.client.configuration.JwtLoginConfigurer;
import com.new4net.jwt.client.configuration.JwtRefreshSuccessHandler;
import com.new4net.jwt.client.filter.OptionsRequestFilter;
import com.new4net.sso.api.JwtUserService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.Header;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
@EnableConfigurationProperties(JwtClientProperties.class)

public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements ResourceLoaderAware, BeanFactoryAware {

    public void configure(WebSecurity web) throws Exception {
        //解决静态资源被拦截的问题
        //web.ignoring().antMatchers("/**", "/kaptcha.jpg");
    }
    @Autowired
    private JwtClientProperties jwtClientProperties;

    protected void configure(HttpSecurity http) throws Exception {
//        http.headers().contentTypeOptions().disable();
        Constants.ModuleName=jwtClientProperties.getModuleName();

        http.authorizeRequests()
                .antMatchers("/","/test/**","/page/**","/src/**","/vendors/**","/jquery-treetable/**","/kaptcha.jpg","/loginByAdmin", "/login", "/actuator/info", "/eureka", "/checkVCode", "/user/regByAccount").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement().disable()
                .cors()
                .and()

                .addFilterAfter(new OptionsRequestFilter(), CorsFilter.class)
                .apply(new JsonLoginConfigurer<>()).loginSuccessHandler(jsonLoginSuccessHandler())
                .and()
//                .apply(new AdminLoginConfigurer<>()).loginSuccessHandler(jsonLoginSuccessHandler())
//                .and()
                .apply(new JwtLoginConfigurer<>()).tokenValidSuccessHandler(jwtRefreshSuccessHandler()).permissiveRequestUrls("/logout", "/**", "/actuator/info", "/eureka", "/login", "/checkVCode", "/user/regByAccount")
                .and()
                .logout()
//		        .logoutUrl("/logout")   //默认就是"/logout"
                .addLogoutHandler(tokenClearLogoutHandler())
                .logoutSuccessHandler(new MyHttpStatusReturningLogoutSuccessHandler())
                .and()
                .sessionManagement().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider()).authenticationProvider(jwtAuthenticationProvider());

    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean("jwtAuthenticationProvider")
    protected AuthenticationProvider jwtAuthenticationProvider() {
        return new ServerJwtAuthenticationProvider(beanFactory.getBean(JwtUserService.class));
    }

    private DaoUserDetailsService daoUserDetailsService() {

        return this.beanFactory.getBean(DaoUserDetailsService.class);
    }

    @Bean("daoAuthenticationProvider")
    protected AuthenticationProvider daoAuthenticationProvider() throws Exception {
        //这里会默认使用BCryptPasswordEncoder比对加密后的密码，注意要跟createUser时保持一致
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(daoUserDetailsService());
        daoProvider.setPasswordEncoder(passwordEncoder());

        return daoProvider;
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return daoUserDetailsService();
    }

    @Bean
    protected JsonLoginSuccessHandler jsonLoginSuccessHandler() {
        return new JsonLoginSuccessHandler(daoUserDetailsService());
    }

    @Bean
    protected JwtRefreshSuccessHandler jwtRefreshSuccessHandler() {
        return new JwtRefreshSuccessHandler(beanFactory.getBean(JwtUserService.class));
    }

    @Bean
    protected TokenClearLogoutHandler tokenClearLogoutHandler() {
        return new TokenClearLogoutHandler(daoUserDetailsService());
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }



    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private ResourceLoader resourceLoader;
//    @Bean
//    public ServletRegistrationBean myServlet() {
//        KaptchaServlet servlet = new KaptchaServlet();
//        ServletRegistrationBean bean = new ServletRegistrationBean(servlet, "/kaptcha.jpg");
//        Map<String, String> initParameters = new HashMap<>();
//        initParameters.put("kaptcha.border", "no");
//        initParameters.put("kaptcha.image.width", "112");
//        initParameters.put("kaptcha.image.height", "50");
//        initParameters.put("kaptcha.textproducer.char.length", "4");
//        initParameters.put("kaptcha.textproducer.font.color", "red");
//        bean.setInitParameters(initParameters);
//        return bean;
//    }
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    public static void main(String[] args){
        String str = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("sysadmin");
        System.out.println(str);
        String str1 = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("sysadmin");
        System.out.println(str1);
    }

}
