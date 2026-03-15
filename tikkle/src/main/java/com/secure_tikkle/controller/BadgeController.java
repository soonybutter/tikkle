package com.secure_tikkle.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.secure_tikkle.service.BadgeService;

@RestController
@RequestMapping("/api/badges")
@lombok.RequiredArgsConstructor
public class BadgeController {

  private final BadgeService badgeService;

  private static Long uid(org.springframework.security.oauth2.core.user.OAuth2User user) {
    return ((Number) user.getAttributes().get("id")).longValue();
  }

  @GetMapping
  public java.util.List<com.secure_tikkle.dto.BadgeDto> myBadges(
      @org.springframework.security.core.annotation.AuthenticationPrincipal
      org.springframework.security.oauth2.core.user.OAuth2User user) {
    return badgeService.listForUser(uid(user)); // ← DTO이므로 Lazy 안터짐
  }
}
