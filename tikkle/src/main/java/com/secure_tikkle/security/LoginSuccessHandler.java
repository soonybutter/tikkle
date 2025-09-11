package com.secure_tikkle.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper mapper = new ObjectMapper();

  
  @Value("${app.frontend-url}")
  private String frontendUrl;

  private static final Pattern ORIGIN_WHITELIST =
      Pattern.compile("^https://([a-z0-9-]+\\.)?tikkle\\.pages\\.dev$|^https://tikkle\\.pages\\.dev$");

  @Override
  public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth) throws IOException {
	  OAuth2User principal =
			    (auth != null && auth.getPrincipal() instanceof OAuth2User o) ? o : null;

    Map<String, Object> attrs = principal != null ? principal.getAttributes() : Collections.emptyMap();
    Map<String, Object> me = new LinkedHashMap<>();
    me.put("id",       attrs.get("id"));
    me.put("provider", attrs.get("provider"));
    me.put("userKey",  attrs.get("userKey"));
    me.put("email",    attrs.get("email"));
    me.put("name",     attrs.get("name"));

    // JSON 원하면 JSON으로
    String accept = req.getHeader("Accept");
    if (accept != null && accept.contains("application/json")) {
      res.setStatus(HttpServletResponse.SC_OK);
      res.setContentType("application/json;charset=UTF-8");
      mapper.writeValue(res.getWriter(), Map.of("ok", true, "me", me));
      return;
    }

    
    String origin = req.getHeader("Origin");
    String target = frontendUrl; // 기본: 프로퍼티 값
    if (origin != null && ORIGIN_WHITELIST.matcher(origin).matches()) {
      target = origin;
    }

    res.sendRedirect(target + "/");
  }
}