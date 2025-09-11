package com.secure_tikkle.config;

import com.secure_tikkle.security.CustomOAuth2UserService;
import com.secure_tikkle.security.LoginSuccessHandler;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final LoginSuccessHandler loginSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ✅ CORS 사용 (아래 CORS Bean과 연결)
            .cors(cors -> {})

            // ✅ CSRF 끄기(REST + 쿠키 사용 시 간단하게)
            .csrf(csrf -> csrf.disable())

            // ✅ 권한 규칙 (OPTIONS, actuator, 공개 API 허용)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()       // ← 프리플라이트 열기
                .requestMatchers("/actuator/**").permitAll()                  // ← health 302 방지
                .requestMatchers(HttpMethod.GET, "/api/me", "/api/health", "/api/news").permitAll()
                .requestMatchers("/", "/login", "/oauth2/**", "/auth/**", "/me",
                                 "/debug/**", "/error", "/share/**",
                                 "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated()
            )

            .oauth2Login(oauth -> oauth
                .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                .successHandler(loginSuccessHandler)
            )

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
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
      CorsConfiguration c = new CorsConfiguration();
      c.setAllowCredentials(true);
      // Cloudflare Pages (프로덕션+프리뷰)만 허용
      c.setAllowedOriginPatterns(List.of(
        "https://tikkle.pages.dev",
        "https://*.tikkle.pages.dev"
      ));
      c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
      c.setAllowedHeaders(List.of("Authorization","Content-Type","X-Requested-With"));
      c.setMaxAge(86400L);

      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", c);
      return source;
    }
}