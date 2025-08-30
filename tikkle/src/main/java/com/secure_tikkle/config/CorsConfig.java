package com.secure_tikkle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.cors.CorsConfigurationSource;


@Configuration
public class CorsConfig {

	  @Bean
	  public CorsConfigurationSource corsConfigurationSource() {
		  
		    var c = new org.springframework.web.cors.CorsConfiguration();
		    c.setAllowedOrigins(java.util.List.of("http://localhost:3000"));
		    c.setAllowedMethods(java.util.List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
		    c.setAllowedHeaders(java.util.List.of("*"));
		    c.setAllowCredentials(true);
	
		    var src = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
		    src.registerCorsConfiguration("/**", c);
		    return src;
		    
	  }
}
