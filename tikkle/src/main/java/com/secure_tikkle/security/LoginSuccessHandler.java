package com.secure_tikkle.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper mapper = new ObjectMapper();

  /**
   * 반드시 운영에서 명시해 주세요. (예: https://tikkle-api.koreacentral.cloudapp.azure.com)
   * 절대 localhost 기본값을 두지 않습니다.
   */
  @Value("${app.frontend-url:}")
  private String frontendUrl;

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

    // 클라이언트가 JSON을 원할 경우 JSON 응답
    String accept = req.getHeader("Accept");
    if (accept != null && accept.contains("application/json")) {
      res.setStatus(HttpServletResponse.SC_OK);
      res.setContentType("application/json;charset=UTF-8");
      mapper.writeValue(res.getWriter(), Map.of("ok", true, "me", me));
      return;
    }

    // 리다이렉트 대상 결정: 1순위 app.frontend-url, 없으면 포워드 헤더 기반 추정
    String targetBase = resolveFrontendBaseUrl(req);
    if (!targetBase.endsWith("/")) targetBase = targetBase + "/";

    res.sendRedirect(targetBase);
  }

  /**
   * app.frontend-url이 비어있으면 X-Forwarded-* 헤더/요청 정보를 이용해 외부 베이스 URL을 추정.
   * (예비용. 운영에서는 app.frontend-url을 꼭 설정하세요.)
   */
  private String resolveFrontendBaseUrl(HttpServletRequest req) {
    if (frontendUrl != null && !frontendUrl.isBlank()) {
      return frontendUrl.trim();
    }

    String proto = headerOr(req, "X-Forwarded-Proto", req.getScheme()); // http/https
    String host  = headerOr(req, "X-Forwarded-Host",  req.getServerName());
    String portH = headerOr(req, "X-Forwarded-Port",  String.valueOf(req.getServerPort()));

    // 표준 포트는 생략
    boolean omitPort = ("https".equalsIgnoreCase(proto) && "443".equals(portH))
                    || ("http".equalsIgnoreCase(proto)  && "80".equals(portH));

    return proto + "://" + host + (omitPort ? "" : (":" + portH));
  }

  private static String headerOr(HttpServletRequest req, String name, String defVal) {
    String v = req.getHeader(name);
    return (v == null || v.isBlank()) ? defVal : v.trim();
  }
}