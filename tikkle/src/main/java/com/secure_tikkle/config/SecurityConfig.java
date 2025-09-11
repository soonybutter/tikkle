
package com.secure_tikkle.config;

import com.secure_tikkle.security.CustomOAuth2UserService;
import com.secure_tikkle.security.LoginSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
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
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 프리플라이트 허용
                .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/me", "/api/health").permitAll()
                .requestMatchers(
                    "/", "/login", "/oauth2/**", "/auth/**", "/me",
                    "/debug/**", "/error", "/share/**",
                    "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**"
                ).permitAll()
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
            )
            // ★★ 반드시 필요
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();

        // 자격증명(JSESSIONID) 허용
        c.setAllowCredentials(true);

        // Pages(프로덕션/프리뷰) + 로컬 개발 허용
        c.setAllowedOriginPatterns(List.of(
            "https://tikkle.pages.dev",
            "https://*.tikkle.pages.dev",
            "http://localhost:5173",
            "http://127.0.0.1:5173"
        ));

        // 메서드/헤더 넉넉히 허용 (프리플라이트 헤더 불일치 방지)
        c.setAllowedMethods(List.of("*"));
        c.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", c);
        return source;
    }
}
