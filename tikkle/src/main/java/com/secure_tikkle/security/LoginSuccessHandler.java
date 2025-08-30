package com.secure_tikkle.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  private static final Logger log = LoggerFactory.getLogger(LoginSuccessHandler.class);
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res,
                                      Authentication auth) throws IOException {
    OAuth2User principal = (auth != null && auth.getPrincipal() instanceof OAuth2User)
        ? (OAuth2User) auth.getPrincipal() : null;

    Map<String, Object> attrs = principal != null ? principal.getAttributes() : Collections.emptyMap();

    // 평탄화된 속성만 사용 (CustomOAuth2UserService에서 넣어준 키들)
    // null 값도 허용하도록 수동 맵 구성 (Map.of 는 null 금지라 쓰지 않음)
    Map<String, Object> me = new LinkedHashMap<>();
    me.put("id",       attrs.get("id"));
    me.put("provider", attrs.get("provider"));
    me.put("userKey",  attrs.get("userKey"));
    me.put("email",    attrs.get("email"));
    me.put("name",     attrs.get("name"));

    // (원하면 여기서 리다이렉트로 바꿔도 됨: res.sendRedirect("/auth/success");)
    res.setStatus(HttpServletResponse.SC_OK);
    res.setContentType("application/json;charset=UTF-8");
    mapper.writeValue(res.getWriter(), Map.of("ok", true, "me", me));

    log.info("OAuth2 login success: provider={}, id={}", me.get("provider"), me.get("id"));
  }
}
