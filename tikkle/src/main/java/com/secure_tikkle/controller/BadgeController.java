package com.secure_tikkle.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.secure_tikkle.dto.UserBadgeDto;
import com.secure_tikkle.repository.UserBadgeRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
public class BadgeController {

	private final UserBadgeRepository userBadges;
	  private static Long uid(OAuth2User u){ return ((Number)u.getAttributes().get("id")).longValue(); }

	  @GetMapping("/me")
	  public List<UserBadgeDto> myBadges(@AuthenticationPrincipal OAuth2User user) {
	    return userBadges.findByUser_IdOrderByEarnedAtDesc(uid(user)).stream()
	        .map(b -> new UserBadgeDto(b.getCode(), b.getName(), b.getIconUrl(), b.getEarnedAt()))
	        .toList();
	  }
	  
}
