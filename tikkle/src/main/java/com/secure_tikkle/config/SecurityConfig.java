package com.secure_tikkle.config;

import com.secure_tikkle.security.CustomOAuth2UserService;
import com.secure_tikkle.security.LoginSuccessHandler;

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
    			  .userInfoEndpoint(ui -> ui.userService(customOAuth2UserService))
    			  .successHandler(loginSuccessHandler)  
    			  .failureUrl("/auth/failure")
    	  )
    	  .logout(l -> l
    			    .logoutUrl("/api/logout")   // 기본: POST로만 처리, deprecated API 없음
    			    .deleteCookies("JSESSIONID")
    			    .invalidateHttpSession(true)
    			    .logoutSuccessHandler((req,res,auth) -> {
    			        res.setStatus(200);
    			        res.getWriter().write("LOGOUT OK");
    			    })
    			  );
            
        http.cors(c -> {});
        http.csrf(csrf -> csrf.disable());
        
        return http.build();
        
        
    }
    
    
}
