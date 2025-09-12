package com.secure_tikkle.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final ObjectMapper mapper = new ObjectMapper();

	@Value("${app.frontend-url:}")
	  private String frontendUrl;

	  private static boolean wantsJson(HttpServletRequest req) {
	    String accept = req.getHeader("Accept");
	    return accept != null && accept.contains("application/json");
	  }

	  private String resolveBase(HttpServletRequest req) {
	    String proto = Optional.ofNullable(req.getHeader("X-Forwarded-Proto")).orElse(req.getScheme());
	    String host  = Optional.ofNullable(req.getHeader("X-Forwarded-Host")).orElse(req.getServerName());
	    String portS = Optional.ofNullable(req.getHeader("X-Forwarded-Port")).orElse(String.valueOf(req.getServerPort()));
	    int port = -1;
	    try { port = Integer.parseInt(portS); } catch (Exception ignored) {}

	    boolean omitPort = ("https".equalsIgnoreCase(proto) && (port == 443 || port == -1))
	                    || ("http".equalsIgnoreCase(proto)  && (port == 80  || port == -1));

	    return proto + "://" + host + (omitPort ? "" : ":" + port);
	  }

	  @Override
	  public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth) throws IOException {
	    OAuth2User principal = (auth != null && auth.getPrincipal() instanceof OAuth2User) ? (OAuth2User) auth.getPrincipal() : null;

	    Map<String, Object> attrs = principal != null ? principal.getAttributes() : Collections.emptyMap();
	    Map<String, Object> me = new LinkedHashMap<>();
	    me.put("id",       attrs.get("id"));
	    me.put("provider", attrs.get("provider"));
	    me.put("userKey",  attrs.get("userKey"));
	    me.put("email",    attrs.get("email"));
	    me.put("name",     attrs.get("name"));

	    if (wantsJson(req)) {
	      res.setStatus(HttpServletResponse.SC_OK);
	      res.setContentType("application/json;charset=UTF-8");
	      mapper.writeValue(res.getWriter(), Map.of("ok", true, "me", me));
	      return;
	    }

	    String target = (frontendUrl != null && !frontendUrl.isBlank())
	        ? frontendUrl
	        : resolveBase(req);

	    if (!target.endsWith("/")) target += "/";

	    log.info("LoginSuccess redirect -> {}", target);
	    res.sendRedirect(target);
	  }
}