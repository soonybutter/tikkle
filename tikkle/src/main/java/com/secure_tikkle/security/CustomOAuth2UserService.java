// security/CustomOAuth2UserService.java
package com.secure_tikkle.security;

import com.secure_tikkle.domain.*;
import com.secure_tikkle.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final UserRepository users;

    @SuppressWarnings("unchecked")
    private static Map<String,Object> map(Object o) {
        return (o instanceof Map) ? (Map<String,Object>) o : Collections.emptyMap();
    }
    private static String str(Object o) { return o == null ? null : String.valueOf(o); }

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {
        OAuth2User o = new DefaultOAuth2UserService().loadUser(req);

        String reg = req.getClientRegistration().getRegistrationId(); // google/naver/kakao
        Provider provider = Provider.valueOf(reg.toUpperCase());
        Map<String,Object> a = o.getAttributes();

        // 1) 공급자별 키/이름/이메일 추출
        String userKey, name, email;
        switch (provider) {
            case GOOGLE -> {
                userKey = str(a.get("sub"));
                name    = Optional.ofNullable(str(a.get("name"))).filter(s -> !s.isBlank()).orElse("google_" + userKey);
                email   = str(a.get("email"));
            }
            case NAVER -> {
                Map<String,Object> res = map(a.get("response"));
                userKey = str(res.get("id"));
                name    = Optional.ofNullable(str(res.get("nickname"))).filter(s -> !s.isBlank()).orElse("naver_" + userKey);
                email   = str(res.get("email"));
            }
            case KAKAO -> {
                userKey = str(a.get("id")); // Long → String
                Map<String,Object> acc = map(a.get("kakao_account"));
                Map<String,Object> profile = map(acc.get("profile"));
                String nick = str(profile.get("nickname"));
                name  = (nick != null && !nick.isBlank()) ? nick : "kakao_" + userKey;
                email = str(acc.get("email")); // 비즈앱/동의 없으면 null 가능
            }
            default -> throw new OAuth2AuthenticationException(new OAuth2Error("unsupported_provider"), "Unsupported provider: " + reg);
        }

        // 2) upsert (provider, userKey)
        User saved = users.findByProviderAndUserKey(provider, userKey)
            .map(u -> {
                u.setName(name);
                if (email != null && !email.isBlank()) u.setEmail(email);
                return users.save(u);
            })
            .orElseGet(() -> users.save(User.builder()
                .provider(provider)
                .userKey(userKey)
                .name(name)
                .email(email)   // null 가능
                .role(Role.USER)
                .build()));

        // 3) 세션에 넣을 "평탄화" 속성 + 권한
        Map<String,Object> flat = new LinkedHashMap<>();
        flat.put("id", saved.getId());                 // ← 우리 DB PK (FE는 이 값만 보면 됨)
        flat.put("provider", saved.getProvider().name());
        flat.put("userKey", saved.getUserKey());
        flat.put("email", saved.getEmail());
        flat.put("name", saved.getName());

        return new DefaultOAuth2User(
            Set.of(new SimpleGrantedAuthority("ROLE_" + saved.getRole().name())),
            flat,
            "id" // principal name attribute
        );
    }
}
