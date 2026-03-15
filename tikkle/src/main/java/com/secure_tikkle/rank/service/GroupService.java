package com.secure_tikkle.rank.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.secure_tikkle.domain.RankGroup;
import com.secure_tikkle.domain.RankGroupMember;
import com.secure_tikkle.domain.RankInvite;
import com.secure_tikkle.rank.repo.RankGroupMemberRepo;
import com.secure_tikkle.rank.repo.RankGroupRepo;
import com.secure_tikkle.rank.repo.RankInviteRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupService {

	private final RankGroupRepo groupRepo;
	  private final RankGroupMemberRepo memberRepo;
	  private final RankInviteRepo inviteRepo;

	  // === 유틸: 초대코드 생성 ===
	  private static final String ALPH = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
	  private static final SecureRandom RNG = new SecureRandom();
	  private String genCode(int len) {
	    StringBuilder sb = new StringBuilder(len);
	    for (int i=0;i<len;i++) sb.append(ALPH.charAt(RNG.nextInt(ALPH.length())));
	    return sb.toString();
	  }

	  @Transactional
	  public RankGroup createGroup(Long ownerId, String name) {
	    RankGroup g = groupRepo.save(RankGroup.builder()
	        .name(name)
	        .ownerId(ownerId)
	        .createdAt(Instant.now())
	        .build());
	    memberRepo.save(RankGroupMember.builder()
	        .groupId(g.getId())
	        .userId(ownerId)
	        .role("OWNER")
	        .joinedAt(Instant.now())
	        .build());
	    return g;
	  }

	  public List<RankGroup> listMyGroups(Long userId) {
	    return memberRepo.findByUserId(userId).stream()
	        .map(m -> groupRepo.findById(m.getGroupId()).orElse(null))
	        .filter(Objects::nonNull)
	        .toList();
	  }

	  @Transactional
	  public RankInvite createInvite(Long userId, Long groupId, Integer ttlHours, Integer maxUses) {
	    // 권한 체크: owner or member 모두 초대 가능하게 할지? 여기선 모두 가능하게.
	    String code;
	    do { code = genCode(8); } while (inviteRepo.findByCode(code).isPresent());

	    Instant exp = (ttlHours == null || ttlHours <= 0) ? null
	        : Instant.now().plus(ttlHours, ChronoUnit.HOURS);

	    return inviteRepo.save(RankInvite.builder()
	        .groupId(groupId)
	        .code(code)
	        .maxUses((maxUses!=null && maxUses>0)? maxUses : null)
	        .usedCount(0)
	        .expiresAt(exp)
	        .createdBy(userId)
	        .createdAt(Instant.now())
	        .build());
	  }

	  @Transactional
	  public RankGroup joinByCode(Long userId, String code) {
	    RankInvite inv = inviteRepo.findByCode(code)
	        .orElseThrow(() -> new NoSuchElementException("invalid_code"));

	    if (inv.getExpiresAt()!=null && Instant.now().isAfter(inv.getExpiresAt()))
	      throw new IllegalStateException("expired");
	    if (inv.getMaxUses()!=null && inv.getUsedCount()>=inv.getMaxUses())
	      throw new IllegalStateException("limit_reached");

	    if (!memberRepo.existsByGroupIdAndUserId(inv.getGroupId(), userId)) {
	      memberRepo.save(RankGroupMember.builder()
	          .groupId(inv.getGroupId())
	          .userId(userId)
	          .role("MEMBER")
	          .joinedAt(Instant.now())
	          .build());
	    }
	    inv.setUsedCount(Optional.ofNullable(inv.getUsedCount()).orElse(0) + 1);
	    inviteRepo.save(inv);

	    return groupRepo.findById(inv.getGroupId()).orElseThrow();
	  }

	  @Transactional
	  public void leave(Long groupId, Long userId) {
	    memberRepo.deleteByGroupIdAndUserId(groupId, userId);
	  }

	  // === 랭킹 결과 DTO ===
	  public record LeaderRow(Long userId, String name, String avatar, long total, long last30d) {}

	  
	   // 실제 합계/최근 30일 합계 집계는 기존 테이블(Goals, SavingsLog)에 맞게 아래 두 콜백을 구현해야함.
	   
	  public List<LeaderRow> leaderboard(Long groupId,
	                                     java.util.function.Function<Long, Map<Long,Long>> totalGetter,
	                                     java.util.function.Function<Long, Map<Long,Long>> last30dGetter,
	                                     java.util.function.Function<Long, String> nameGetter,
	                                     java.util.function.Function<Long, String> avatarGetter) {
	    List<Long> userIds = memberRepo.findByGroupId(groupId).stream()
	        .map(RankGroupMember::getUserId).toList();

	    Map<Long, Long> total = totalGetter.apply(groupId);   // userId -> 총합
	    Map<Long, Long> last30 = last30dGetter.apply(groupId);// userId -> 최근 30일

	    return userIds.stream().map(uid -> new LeaderRow(
	        uid,
	        Optional.ofNullable(nameGetter.apply(uid)).orElse("사용자 "+uid),
	        avatarGetter.apply(uid),
	        Optional.ofNullable(total.get(uid)).orElse(0L),
	        Optional.ofNullable(last30.get(uid)).orElse(0L)
	    )).sorted(Comparator.comparingLong(LeaderRow::total).reversed())
	      .toList();
	  }
}
