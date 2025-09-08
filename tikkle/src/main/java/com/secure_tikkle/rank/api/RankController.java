package com.secure_tikkle.rank.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*; 

import com.secure_tikkle.domain.RankGroup;
import com.secure_tikkle.domain.RankInvite;
import com.secure_tikkle.rank.repo.AggregationRepo;
import com.secure_tikkle.rank.service.GroupService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/rank")
@RequiredArgsConstructor
public class RankController {

  private final GroupService svc;
  private final AggregationRepo agRepo; 

  @PostMapping("/groups")
  public RankGroup create(
      @AuthenticationPrincipal(expression = "attributes['id']") Long userId,
      @RequestBody Map<String, String> body) {
    String name = body.getOrDefault("name", "우리 랭킹");
    return svc.createGroup(userId, name);
  }

  @GetMapping("/groups")
  public List<RankGroup> myGroups(
      @AuthenticationPrincipal(expression = "attributes['id']") Long userId) {
    return svc.listMyGroups(userId);
  }

  @PostMapping("/groups/{id}/invite")
  public RankInvite invite(
      @AuthenticationPrincipal(expression = "attributes['id']") Long userId,
      @PathVariable Long id,
      @RequestBody(required = false) Map<String, Object> body) {
    Integer ttl = body != null ? (Integer) body.getOrDefault("ttlHours", 72) : 72;
    Integer maxUses = body != null ? (Integer) body.getOrDefault("maxUses", 50) : 50;
    return svc.createInvite(userId, id, ttl, maxUses);
  }

  @PostMapping("/join")
  public RankGroup join(
      @AuthenticationPrincipal(expression = "attributes['id']") Long userId,
      @RequestBody Map<String, String> body) {
    return svc.joinByCode(userId, body.get("code"));
  }

  @DeleteMapping("/groups/{id}/leave")
  public Map<String, Object> leave(
      @AuthenticationPrincipal(expression = "attributes['id']") Long userId,
      @PathVariable Long id) {
    svc.leave(id, userId);
    return Map.of("ok", true);
  }

  /** 랭킹 보기: total(누적), last30d(최근 30일) */
  @GetMapping("/groups/{id}")
  public Map<String, Object> groupDetail(
      @AuthenticationPrincipal(expression = "attributes['id']") Long userId,
      @PathVariable Long id) {

    // AggregationRepo(B안 버전)의 LeaderRow(public final 필드) → 바로 직렬화 가능
    var rows = agRepo.findGroupLeaders(id, LocalDateTime.now().minusDays(30))
        .stream()
        .map(r -> Map.of(
            "userId",  r.userId,
            "name",    r.name,
            "total",   r.total,
            "last30d", r.last30d
        ))
        .toList();

    return Map.of("id", id, "members", rows);
  }
}