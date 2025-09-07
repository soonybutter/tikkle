package com.secure_tikkle.config;

import com.secure_tikkle.security.CustomOAuth2UserService;
import com.secure_tikkle.security.LoginSuccessHandler;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private final LoginSuccessHandler loginSuccessHandler;
    
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	http
    	  .authorizeHttpRequests(auth -> auth
    	      .requestMatchers("/", "/login", "/oauth2/**",
    	                       "/auth/**", "/me", "/api/me",
    	                       "/api/health",
    	                       "/debug/**", "/error",
    	                       "/share/",
    	                       "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()   
    	      .anyRequest().authenticated()
    	  )
    	  .oauth2Login(oauth -> oauth
    			  .successHandler((req, res, auth) -> {
		          String front = System.getenv().getOrDefault("FRONT_ORIGIN", "http://localhost:5173");
		          res.sendRedirect(front + "/"); //  로그인 성공 시 홈으로
    			  })
    	  )
    	  .logout(logout -> logout
    	            .logoutUrl("/api/logout")           // 로그아웃 URL
    	            .deleteCookies("JSESSIONID")        // 세션 쿠키 제거
    	            .invalidateHttpSession(true)        // 서버 세션 무효화
    	            .clearAuthentication(true)
    	            .logoutSuccessHandler((req, res, auth) -> {
    	                res.setStatus(HttpServletResponse.SC_OK);
    	                res.setContentType("application/json;charset=UTF-8");
    	                res.getWriter().write("{\"ok\":true}");
    	  })
    	);
            
        http.cors(c -> {});
        http.csrf(csrf -> csrf.disable());
        
        return http.build();
        
        
    }
    
    
}
