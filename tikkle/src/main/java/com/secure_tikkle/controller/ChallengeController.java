package com.secure_tikkle.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.secure_tikkle.dto.ChallengeDtos.CreateChallengeRequest;
import com.secure_tikkle.dto.ChallengeDtos.JoinChallengeRequest;
import com.secure_tikkle.dto.ChallengeDtos.LeaderboardRow;
import com.secure_tikkle.service.ChallengeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

	private final ChallengeService service;

	  private static Long uid(OAuth2User user) {
		  
	    return ((Number) user.getAttributes().get("id")).longValue();
	    
	  }

	  // 챌린지 생성 
	  @PostMapping
	  public Object create(@Valid @RequestBody CreateChallengeRequest req) {
	    return service.create(req);
	  }

	  // 챌린지 참여 
	  @PostMapping("/{id}/join")
	  public Object join(@AuthenticationPrincipal OAuth2User user,
	                     @PathVariable Long id,
	                     @Valid @RequestBody JoinChallengeRequest req) {
	    return service.join(uid(user), id, req);
	  }

	  // 리더보드 
	  @GetMapping("/{id}/leaderboard")
	  public List<LeaderboardRow> leaderboard(@PathVariable Long id) {
	    return service.leaderboard(id);
	  }
	  
}
