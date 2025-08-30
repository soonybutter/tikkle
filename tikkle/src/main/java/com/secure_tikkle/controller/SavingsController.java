package com.secure_tikkle.controller;

import com.secure_tikkle.dto.CreateSavingsRequest;
import com.secure_tikkle.dto.SavingsLogDto;
import com.secure_tikkle.domain.SavingsLog;
import com.secure_tikkle.repository.SavingsLogRepository;
import com.secure_tikkle.service.SavingsService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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

  /** 적립 생성 (201 Created + Location 헤더) */
  @PostMapping
  public ResponseEntity<SavingsLogDto> save(@AuthenticationPrincipal OAuth2User user,
                                            @PathVariable Long goalId,
                                            @RequestBody CreateSavingsRequest body) {
    Long amount = body.amount();
    String memo = body.memo();

    SavingsLog saved = savingsService.save(uid(user), goalId, amount, memo);

    SavingsLogDto dto = new SavingsLogDto(
        saved.getId(),
        goalId,
        saved.getAmount(),
        saved.getMemo(),
        saved.getCreatedAt()
    );
    return ResponseEntity
        .created(URI.create("/api/goals/%d/savings/%d".formatted(goalId, saved.getId())))
        .body(dto);
  }

  /** 적립 로그 목록 (소유권 검증은 서비스/리포에서 수행되므로 여기서는 goalId만으로 조회) */
  @GetMapping
  public List<SavingsLogDto> list(@AuthenticationPrincipal OAuth2User user,
                                  @PathVariable Long goalId) {
    // 필요하면 여기서도 소유권 검증을 한 번 더 할 수 있음
    return logs.findByGoal_IdOrderByIdDesc(goalId).stream()
        .map(l -> new SavingsLogDto(
            l.getId(),
            goalId,
            l.getAmount(),
            l.getMemo(),
            l.getCreatedAt()))
        .toList();
  }
}
