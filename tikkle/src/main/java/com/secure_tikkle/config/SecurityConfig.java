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
            // ✅ 반드시 켜기: 우리가 아래 CORS Bean을 쓰게 함
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                // ✅ 프리플라이트는 무조건 통과
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ✅ 헬스/인포는 공개
                .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()

                // ✅ 비로그인에서도 호출할 수 있게 공개
                .requestMatchers(HttpMethod.GET, "/api/me", "/api/health").permitAll()

                // ✅ 로그인/리다이렉트 경로 공개
                .requestMatchers("/oauth2/**", "/login/**").permitAll()

                // 나머지는 인증 필요
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

    // ✅ 여기서만 CORS 제어 (Nginx는 CORS 제거했으므로)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowCredentials(true);
        // Cloudflare Pages 정식/프리뷰 모두 허용
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