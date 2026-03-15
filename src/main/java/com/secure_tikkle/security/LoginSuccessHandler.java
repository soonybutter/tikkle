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

    Map<String, Object> me = new LinkedHashMap<>();
    me.put("id",       attrs.get("id"));
    me.put("provider", attrs.get("provider"));
    me.put("userKey",  attrs.get("userKey"));
    me.put("email",    attrs.get("email"));
    me.put("name",     attrs.get("name"));

    log.info("OAuth2 login success: provider={}, id={}", me.get("provider"), me.get("id"));

    //  브라우저면 SPA 메인으로 이동

    String accept = req.getHeader("Accept");
    String fetchMode = req.getHeader("Sec-Fetch-Mode"); // 있으면 더 정확

    boolean isBrowserNav =
    (fetchMode != null && fetchMode.equalsIgnoreCase("navigate")) ||
    (accept != null && (accept.contains("text/html") || accept.contains("application/xhtml+xml")));

    if (isBrowserNav) {
     res.sendRedirect("/");   // nginx가 /main -> index.html 로 처리하니까 OK
     return;
    }

    boolean wantsJson = accept != null && accept.contains("application/json");


    // 혹시 API 호출이면 JSON 응답 유지
    res.setStatus(HttpServletResponse.SC_OK);
    res.setContentType("application/json;charset=UTF-8");

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("ok", true);
    body.put("me", me);

    mapper.writeValue(res.getWriter(), body);
  }
}
