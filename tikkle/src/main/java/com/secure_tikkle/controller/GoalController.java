package com.secure_tikkle.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

import org.springframework.web.bind.annotation.*;


import com.secure_tikkle.dto.CreateGoalRequest;
import com.secure_tikkle.dto.CreateSavingsLogRequest;
import com.secure_tikkle.dto.GoalDetail;

import com.secure_tikkle.dto.GoalSummaryDto;
import com.secure_tikkle.dto.SavingsLogDto;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.secure_tikkle.service.GoalService;


@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

	private final GoalService goalService;

    private static Long uid(OAuth2User u) {
        return ((Number) u.getAttributes().get("id")).longValue();
    }

    // 1) 내 목표 목록
    @GetMapping
    public List<GoalSummaryDto> list(@AuthenticationPrincipal OAuth2User user) {
        return goalService.list(uid(user));
    }

    // 2) 단건 상세
    @GetMapping("/{id}")
    public GoalDetail detail(@AuthenticationPrincipal OAuth2User user,
                             @PathVariable Long id) {
        return goalService.detail(uid(user), id);
    }

    // 3) 로그 페이지
    @GetMapping("/{id}/logs")
    public Page<SavingsLogDto> logs(@AuthenticationPrincipal OAuth2User user,
                                    @PathVariable Long id,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        return goalService.logs(uid(user), id, page, size);
    }

    // 4) 생성
    @PostMapping
    public ResponseEntity<GoalSummaryDto> create(@AuthenticationPrincipal OAuth2User user,
                                                 @Valid @RequestBody CreateGoalRequest body) {
        GoalSummaryDto created = goalService.create(uid(user), body);
        return ResponseEntity
                .created(URI.create("/api/goals/" + created.id()))
                .body(created);
    }

    // 5) 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal OAuth2User user,
                                       @PathVariable Long id) {
        goalService.deleteGoal(uid(user), id);
        return ResponseEntity.noContent().build();
    }
    
    // 6) 수정
    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(@AuthenticationPrincipal OAuth2User user,
                                       @PathVariable Long id,
                                       @RequestBody UpdateGoalRequest body) {
      goalService.updateGoal(uid(user), id, body.title(), body.targetAmount());
      return ResponseEntity.noContent().build();
    }

    public record UpdateGoalRequest(String title, Long targetAmount) {}
  
}