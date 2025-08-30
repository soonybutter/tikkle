package com.secure_tikkle.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.secure_tikkle.repository.GoalRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
public class ShareController {

	  private final GoalRepository goals;

	  private static Long uid(OAuth2User u){ 
		  return ((Number)u.getAttributes().get("id")).longValue(); 
	  }

	  // 카카오 공유에 쓸 간단 데이터 
	  @GetMapping("/goal/{id}")
	  public Map<String,Object> shareGoal(@AuthenticationPrincipal OAuth2User user, @PathVariable Long id){
	    var g = goals.findByIdAndUser_Id(id, uid(user)).orElseThrow();
	    long current = g.getCurrentAmount()==null?0L:g.getCurrentAmount();
	    long target  = g.getTargetAmount()==null?0L:g.getTargetAmount();
	    int progress = target==0?0:(int)Math.min(100, current*100/target);
	    return Map.of(
	      "title", "티끌 모아 태산 — " + g.getTitle(),
	      "description", "진행률 " + progress + "% ("+current+" / "+target+"원)",
	      "imageUrl", "https://dummyimage.com/600x400/ddd/000&text=tikkle", // 임시
	      "link", "https://yourapp.example/goal/"+g.getId()
	    );
	  }
	  
}
