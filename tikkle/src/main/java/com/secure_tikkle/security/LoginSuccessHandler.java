package com.secure_tikkle.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final ObjectMapper mapper = new ObjectMapper();

	  @Override
	  public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth) throws IOException {
	    OAuth2User principal = (auth != null && auth.getPrincipal() instanceof OAuth2User o) ? o : null;

	    String accept = req.getHeader("Accept");
	    if (accept != null && accept.contains("application/json")) {
	      Map<String, Object> me = Map.of(
	        "id",       principal != null ? principal.getAttributes().get("id")       : null,
	        "provider", principal != null ? principal.getAttributes().get("provider") : null,
	        "userKey",  principal != null ? principal.getAttributes().get("userKey")  : null,
	        "email",    principal != null ? principal.getAttributes().get("email")    : null,
	        "name",     principal != null ? principal.getAttributes().get("name")     : null
	      );
	      res.setStatus(HttpServletResponse.SC_OK);
	      res.setContentType("application/json;charset=UTF-8");
	      mapper.writeValue(res.getWriter(), Map.of("ok", true, "me", me));
	      return;
	    }

	    String scheme = Optional.ofNullable(req.getHeader("X-Forwarded-Proto")).orElse(req.getScheme());
	    String host   = Optional.ofNullable(req.getHeader("X-Forwarded-Host")).orElse(req.getServerName());
	    String port   = Optional.ofNullable(req.getHeader("X-Forwarded-Port")).orElse(String.valueOf(req.getServerPort()));

	    String target = scheme + "://" + host;
	    if (("http".equalsIgnoreCase(scheme) && !"80".equals(port)) ||
	        ("https".equalsIgnoreCase(scheme) && !"443".equals(port))) {
	      target += ":" + port;
	    }
	    res.sendRedirect(target + "/");
	  }
}