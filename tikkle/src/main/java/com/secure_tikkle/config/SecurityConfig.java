package com.secure_tikkle.config;

import com.secure_tikkle.security.CustomOAuth2UserService;
import com.secure_tikkle.security.LoginSuccessHandler;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// CORS Bean에 필요
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final LoginSuccessHandler loginSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
        		  
        	.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()	  
            // ✅ 헬스/인포는 무조건 공개
            .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()

            // 공개 API
            .requestMatchers(HttpMethod.GET, "/api/news").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/me", "/api/health").permitAll()

            // 정적/공개 라우트
            .requestMatchers(
              "/", "/login", "/oauth2/**", "/auth/**", "/me",
              "/debug/**", "/error", "/share/**",
              "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**"
            ).permitAll()

            // 나머지는 인증
            .anyRequest().authenticated()
          )

          // OAuth2: 사용자 정보 서비스 + 성공 핸들러 사용
          .oauth2Login(oauth -> oauth
            .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
            .successHandler(loginSuccessHandler)
          )

          // 로그아웃 응답 JSON
          .logout(logout -> logout
            .logoutUrl("/api/logout")
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .logoutSuccessHandler((req, res, auth) -> {
              res.setStatus(HttpServletResponse.SC_OK);
              res.setContentType("application/json;charset=UTF-8");
              res.getWriter().write("{\"ok\":true}");
            })
          )

          // CORS/CSRF
          .cors(cors -> {})     // 아래 corsConfigurationSource() Bean을 사용
          .csrf(csrf -> csrf.disable());

        return http.build();
    }

    
}