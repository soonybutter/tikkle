package com.secure_tikkle.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.secure_tikkle.domain.Goal;
import com.secure_tikkle.domain.SavingsLog;
import com.secure_tikkle.dto.CreateGoalRequest;
import com.secure_tikkle.dto.CreateSavingsLogRequest;
import com.secure_tikkle.dto.GoalDetail;

import com.secure_tikkle.dto.GoalSummaryDto;
import com.secure_tikkle.dto.SavingsLogDto;
import com.secure_tikkle.dto.UpdateSavingsLogRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.secure_tikkle.service.GoalService;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GoalController {

  private final GoalService goalService;

  // CustomOAuth2UserService에서 넣어준 평탄화 속성(우리 DB PK)
  private static Long uid(OAuth2User user) {
    return ((Number) user.getAttributes().get("id")).longValue();
  }

  // 목표 생성 
  @PostMapping("/goals")
  public GoalSummaryDto create(@AuthenticationPrincipal OAuth2User user,
                               @Valid @RequestBody CreateGoalRequest req) {
      return goalService.create(uid(user), req);   // DTO 반환
  }

  // 내 목표 목록 (요약 + 진행률) 
  @GetMapping("/goals")
  public List<GoalSummaryDto> list(@AuthenticationPrincipal OAuth2User user) {
    return goalService.list(uid(user));
  }

  // 목표 상세 (진행률 포함) 
  @GetMapping("/goals/{id}")
  public GoalDetail detail(@AuthenticationPrincipal OAuth2User user,
                           @PathVariable Long id) {
    return goalService.detail(uid(user), id);
  }

  // 절약 저금(저축 로그) 기록 
  @PostMapping("/savings-logs")
  public SavingsLogDto add(@AuthenticationPrincipal OAuth2User user,
                           @Valid @RequestBody CreateSavingsLogRequest req) {
    var saved = goalService.addLog(uid(user), req);
    
    return new SavingsLogDto(
        saved.getId(),
        req.goalId(),               
        saved.getAmount(),
        saved.getMemo(),
        saved.getCreatedAt()
    );
  }

  // 목표별 저축 로그 목록 (페이징) 
  @GetMapping("/goals/{id}/logs")
  public Page<SavingsLogDto> logs(@AuthenticationPrincipal OAuth2User user,
                                  @PathVariable Long id,
                                  @RequestParam(defaultValue="0") int page,
                                  @RequestParam(defaultValue="10") int size) {
    return goalService.logs(uid(user), id, page, size);
  }

  // 저축 로그 수정 
  @PatchMapping("/savings-logs/{logId}")
  public SavingsLogDto update(@AuthenticationPrincipal OAuth2User user,
                              @PathVariable Long logId,
                              @RequestBody Map<String,Object> body) {
    Long amount = body.get("amount")==null?null:((Number)body.get("amount")).longValue();
    String memo = (String) body.get("memo");
    var updated = goalService.updateLog(uid(user), logId, amount, memo);
    return new SavingsLogDto(
        updated.getId(),
        updated.getGoal().getId(),   // 식별자 접근은 LAZY 초기화 없이 안전하도록
        updated.getAmount(),
        updated.getMemo(),
        updated.getCreatedAt()
    );
  }

  // 저축 로그 삭제 
  @DeleteMapping("/savings-logs/{logId}")
  public Map<String,Object> delete(@AuthenticationPrincipal OAuth2User user,
                                   @PathVariable Long logId) {
    goalService.deleteLog(uid(user), logId);
    return Map.of("ok", true);
  }

  
}