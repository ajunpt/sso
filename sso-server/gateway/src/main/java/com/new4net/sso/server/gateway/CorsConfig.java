package com.new4net.sso.server.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * springboot解决跨域问题
 * 
 * @className: CorsConfig
 * @description:TODO
 * @author: jimengkeji
 * @date: 2020年1月2日 下午5:04:29
 */
//@Configuration
public class CorsConfig {
	private CorsConfiguration buildConfig() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		// * 表示对所有的地址都可以访问
		// corsConfiguration.addAllowedOrigin("http://localhost:3030");

		corsConfiguration.setAllowCredentials(true);   
	    //corsConfiguration.addAllowedOrigin("http://localhost:9000");
		corsConfiguration.addAllowedOrigin("*");
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedMethod("*");
		return corsConfiguration;
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", buildConfig());
		return new CorsFilter(source);
	}
//	@Bean
//	public FilterRegistrationBean corsFilter() {
//	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//	    CorsConfiguration config = new CorsConfiguration();
//	    config.setAllowCredentials(true);   
//	    //config.addAllowedOrigin("http://localhost:9000");
//	    config.addAllowedOrigin("null");
//	    config.addAllowedHeader("*");
//	    config.addAllowedMethod("*");
//	    source.registerCorsConfiguration("/**", config); // CORS 配置对所有接口都有效
//	    FilterRegistrationBean bean = newFilterRegistrationBean(new CorsFilter(source));
//	    bean.setOrder(0);
//	    return bean;
//	}
}
