package com.new4net.jwt.client.configuration;


import com.new4net.jwt.client.filter.OptionsRequestFilter;
import com.new4net.jwt.client.service.JwtAuthenticationProvider;
import com.new4net.sso.api.JwtUserService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.Header;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
@EnableConfigurationProperties(JwtClientProperties.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements RequestInterceptor,ResourceLoaderAware, BeanFactoryAware {
    @Autowired
    private JwtClientProperties jwtClientProperties;
    public void configure(WebSecurity web) throws Exception {
        //解决静态资源被拦截的问题
        web.ignoring().antMatchers("/static/**", "/kaptcha.jpg","/actuator/**","/eureka");
    }

    protected void configure(HttpSecurity http) throws Exception {
//        http.addFilterBefore(validateCodeFilter, LogoutFilter.class);
//        validateCodeFilter.setLoginUrl("/login");
        http.headers().contentTypeOptions().disable();
        Constants.ModuleName=jwtClientProperties.getModuleName();

        http.authorizeRequests()
                .antMatchers("/static/**","/actuator/info","/eureka", "/login", "/checkVCode", "/user/regByAccount").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().requireCsrfProtectionMatcher(new CsrfSecurityRequestMatcher()).disable()
                .formLogin().disable()
                .sessionManagement().disable()
                .cors()
                .and()
                .httpBasic().disable()
                .headers().addHeaderWriter(new StaticHeadersWriter(Arrays.asList(
                new Header("Access-control-Allow-Origin", "*"),
                new Header("Access-Control-Expose-Headers", "Authorization"))))
                .and()
                .addFilterAfter(new OptionsRequestFilter(), CorsFilter.class)
                .apply(new JwtLoginConfigurer<>()).tokenValidSuccessHandler(jwtRefreshSuccessHandler()).permissiveRequestUrls("/logout","/static/**","/actuator/info","/eureka", "/login", "/checkVCode", "/user/regByAccount")
                .and()
                .logout();
//		        .logoutUrl("/logout")   //默认就是"/logout"
//                .addLogoutHandler(tokenClearLogoutHandler())
//                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
    }
    @Bean
    protected TokenClearLogoutHandler tokenClearLogoutHandler() {
        return new TokenClearLogoutHandler(beanFactory.getBean(JwtUserService.class));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(jwtAuthenticationProvider());

    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean("jwtAuthenticationProvider")
    protected AuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(beanFactory.getBean(JwtUserService.class));
    }


    public UserDetailsService userDetailsService() {

        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return beanFactory.getBean(JwtUserService.class).loadUserByUsername(username+";"+Constants.ModuleName);
            }
        };
    }

    @Bean
    protected JwtRefreshSuccessHandler jwtRefreshSuccessHandler() {
        return new JwtRefreshSuccessHandler(beanFactory.getBean(JwtUserService.class));
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "HEAD", "OPTION"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        if(Constants.Authorization.get()!=null){
            requestTemplate.header("Authorization",Constants.Authorization.get());
        }
    }
}
