package com.secure_tikkle.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import com.secure_tikkle.domain.Provider;
import com.secure_tikkle.domain.User;
import com.secure_tikkle.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CurrentUserResolver {
	
	private final UserRepository users;

    @SuppressWarnings("unchecked")
    public User resolve(@AuthenticationPrincipal OAuth2User p) {
        if (p == null) return null;
        
        Map<String,Object> a = p.getAttributes();

        // email 우선
        String email = (String) a.get("email");
        if (email != null && !email.isBlank()) {
            return users.findByEmail(email).orElse(null);
        }

        // provider+userKey로
        if (a.get("sub") != null) { // google
            return users.findByProviderAndUserKey(Provider.GOOGLE, String.valueOf(a.get("sub"))).orElse(null);
        }
        if (a.get("response") instanceof Map<?,?> respMap) { // naver
            Object id = respMap.get("id");
            if (id != null)
                return users.findByProviderAndUserKey(Provider.NAVER, String.valueOf(id)).orElse(null);
        }
        if (a.get("id") != null) { // kakao
            return users.findByProviderAndUserKey(Provider.KAKAO, String.valueOf(a.get("id"))).orElse(null);
        }
        return null;
        
        
    }
}
