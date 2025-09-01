package com.secure_tikkle.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
public class CorsConfig {

	@Bean
	  public CorsConfigurationSource corsConfigurationSource() {
		
	    var c = new CorsConfiguration();
	    c.setAllowCredentials(true); // 프론트에서 credentials: 'include' 사용 시 필수

	    // 개발 편의 위해 5173, 5174 , 127.0.0.1 허용
	    c.setAllowedOriginPatterns(List.of(
	        "http://localhost:*",
	        "http://127.0.0.1:*"
	    ));

	    c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
	    c.setAllowedHeaders(List.of("*"));
	    // 필요시 노출 헤더
	    // c.setExposedHeaders(List.of("Set-Cookie"));

	    var src = new UrlBasedCorsConfigurationSource();
	    src.registerCorsConfiguration("/**", c);
	    
	    return src;
	  }
}
