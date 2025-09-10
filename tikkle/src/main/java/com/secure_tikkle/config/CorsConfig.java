package com.secure_tikkle.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig {

  // 전역 CORS 설정 (Security의 http.cors()가 이 Bean을 사용)
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration c = new CorsConfiguration();
    c.setAllowCredentials(true);
    c.setAllowedOriginPatterns(List.of(
        "https://*.tikkle.pages.dev", // 프리뷰 포함
        "https://tikkle.pages.dev",   // 프로덕션
        "http://localhost:*",
        "http://127.0.0.1:*"
    ));
    c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
    c.setAllowedHeaders(List.of("*"));

    UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
    src.registerCorsConfiguration("/**", c);
    return src;
  }

  // 보수책: MVC 레벨에서도 동일 매핑 추가(일부 조합에서 도움됨)
  @Bean
  public WebMvcConfigurer webMvcCors() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
          .allowedOriginPatterns(
              "https://*.tikkle.pages.dev",
              "https://tikkle.pages.dev",
              "http://localhost:*",
              "http://127.0.0.1:*"
          )
          .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS")
          .allowedHeaders("*")
          .allowCredentials(true);
      }
    };
  }
}