// src/main/java/com/secure_tikkle/controller/OAuthDebugController.java
/*
package com.secure_tikkle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class OAuthDebugController {

    @Autowired(required = false)
    private ClientRegistrationRepository repo;

    @Autowired
    private Environment env;

    // A) 등록된 클라이언트 목록 (안전하게)
    @GetMapping("/debug/oauth/clients")
    public Map<String, Object> clients() {
        List<String> ids = new ArrayList<>();
        if (repo == null) {
            return Map.of("ok", false, "reason", "ClientRegistrationRepository bean not found");
        }
        try {
            // InMemoryClientRegistrationRepository는 Iterable<ClientRegistration>을 구현함
            if (repo instanceof Iterable<?> iterable) {
                for (Object o : iterable) {
                    if (o instanceof ClientRegistration cr) {
                        ids.add(cr.getRegistrationId());
                    }
                }
                return Map.of("ok", true, "clients", ids);
            } else {
                // Iterable이 아니면 개별 조회만 시도
                String[] candidates = {"google", "naver", "kakao"};
                for (String id : candidates) {
                    try {
                        ClientRegistration cr = repo.findByRegistrationId(id);
                        if (cr != null) ids.add(cr.getRegistrationId());
                    } catch (Exception ignored) {}
                }
                return Map.of("ok", true, "clients", ids, "note",
                        "repo is not Iterable; probed known ids");
            }
        } catch (Exception e) {
            return Map.of("ok", false, "error", e.getClass().getName(), "message", e.getMessage());
        }
    }

    // B) 카카오만 콕 집어서 확인
    @GetMapping("/debug/oauth/kakao")
    public Map<String, Object> kakao() {
        if (repo == null) return Map.of("ok", false, "exists", false, "reason", "repo null");
        try {
            ClientRegistration r = repo.findByRegistrationId("kakao");
            if (r == null) return Map.of("ok", true, "exists", false);
            return Map.of(
                "ok", true,
                "exists", true,
                "clientName", r.getClientName(),
                "redirectUri", r.getRedirectUri(),
                "authorizationGrantType", r.getAuthorizationGrantType().getValue(),
                "scopes", r.getScopes()
            );
        } catch (Exception e) {
            return Map.of("ok", false, "error", e.getClass().getName(), "message", e.getMessage());
        }
    }

    // C) 실제로 읽힌 프로퍼티 키 확인 (마스킹)
    @GetMapping("/debug/oauth/properties")
    public Map<String, Object> props() {
        String[] keys = new String[] {
            "spring.security.oauth2.client.registration.kakao.client-id",
            "spring.security.oauth2.client.registration.kakao.client-secret",
            "spring.security.oauth2.client.registration.kakao.redirect-uri",
            "spring.security.oauth2.client.provider.kakao.authorization-uri",
            "spring.security.oauth2.client.provider.kakao.token-uri",
            "spring.security.oauth2.client.provider.kakao.user-info-uri",
            "spring.security.oauth2.client.provider.kakao.user-name-attribute",
        };
        Map<String,Object> out = new LinkedHashMap<>();
        for (String k : keys) {
            String v = env.getProperty(k);
            if (v == null) out.put(k, null);
            else if (k.endsWith("client-secret")) out.put(k, "****(masked)****");
            else out.put(k, v);
        }
        return out;
    }
}
*/
