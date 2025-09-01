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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final ObjectMapper mapper = new ObjectMapper();

	  // 프론트 개발 서버 주소 (기본값 5173)
	  @Value("${app.frontend-url:http://localhost:5173}")
	  private String frontendUrl;

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

	    // 브라우저가 아닌 경우(예: curl, Postman에서 Accept: application/json로 요청)엔 JSON 반환
	    String accept = req.getHeader("Accept");
	    boolean wantsJson = accept != null && accept.contains("application/json");
	    if (wantsJson) {
	      res.setStatus(HttpServletResponse.SC_OK);
	      res.setContentType("application/json;charset=UTF-8");
	      mapper.writeValue(res.getWriter(), Map.of("ok", true, "me", me));
	      return;
	    }

	    // 기본: 프론트 홈으로 리다이렉트
	    res.sendRedirect(frontendUrl + "/");
	  }
}
