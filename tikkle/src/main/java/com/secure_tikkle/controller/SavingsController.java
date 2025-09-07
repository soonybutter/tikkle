package com.secure_tikkle.controller;

import com.secure_tikkle.dto.CreateSavingsRequest;
import com.secure_tikkle.dto.SavingsLogDto;
import com.secure_tikkle.repository.SavingsLogRepository;
import com.secure_tikkle.service.SavingsService;


import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals/{goalId}/savings")
@RequiredArgsConstructor
public class SavingsController {

	  private final SavingsService savingsService;
	  private final SavingsLogRepository logs;
	
	  private static Long uid(OAuth2User u){
	    return ((Number)u.getAttributes().get("id")).longValue();
	  }

	  //수정
	 @PatchMapping("/{id}")
	 public ResponseEntity<Void> update(@AuthenticationPrincipal OAuth2User user,
	                                    @PathVariable Long goalId,   // 소유권 체크에 쓰진 않지만 URL 일관성 목적
	                                    @PathVariable Long id,
	                                    @RequestBody SavingsUpdateRequest body) {
	   savingsService.updateLog(uid(user), id, body.amount(), body.memo());
	   return ResponseEntity.noContent().build();
	 }

	//삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@AuthenticationPrincipal OAuth2User user,
	                                   @PathVariable Long goalId,
	                                   @PathVariable Long id) {
	  savingsService.deleteLog(uid(user), id);
	  return ResponseEntity.noContent().build();
	}
	
	@PostMapping
	public ResponseEntity<SavingsLogDto> create(
	    @AuthenticationPrincipal OAuth2User user,
	    @PathVariable Long goalId,
	    @RequestBody CreateSavingsRequest body
	) {
	    Long userId = uid(user);
	    var saved = savingsService.createLog(userId, goalId, body.amount(), body.memo());
	    var dto = new SavingsLogDto(
	        saved.getId(), goalId, saved.getAmount(), saved.getMemo(), saved.getCreatedAt()
	    );
	    return ResponseEntity.status(201).body(dto);
	}
	 

	 public record SavingsUpdateRequest(Long amount, String memo) {}
	
	  // 적립 로그 목록 (내 목표에 대한 것만)
	  @GetMapping
	  public List<SavingsLogDto> list(
	      @AuthenticationPrincipal OAuth2User user,
	      @PathVariable Long goalId
	  ) {
	    Long userId = uid(user);
	    //  소유자 검증 포함 메서드 사용
	    return logs.findByGoal_IdAndGoal_User_IdOrderByIdDesc(goalId, userId).stream()
	        .map(l -> new SavingsLogDto(
	            l.getId(),
	            goalId,
	            l.getAmount(),
	            l.getMemo(),
	            l.getCreatedAt()))
	        .toList();
	  }
}