package com.secure_tikkle.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.secure_tikkle.domain.Challenge;
import com.secure_tikkle.domain.ChallengeMember;
import com.secure_tikkle.domain.Goal;
import com.secure_tikkle.domain.User;
import com.secure_tikkle.dto.ChallengeDtos.CreateChallengeRequest;
import com.secure_tikkle.dto.ChallengeDtos.JoinChallengeRequest;
import com.secure_tikkle.dto.ChallengeDtos.LeaderboardRow;
import com.secure_tikkle.repository.ChallengeMemberRepository;
import com.secure_tikkle.repository.ChallengeRepository;
import com.secure_tikkle.repository.GoalRepository;
import com.secure_tikkle.repository.UserRepository;


import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class ChallengeService {
	
	  private final ChallengeRepository challenges;
	  private final ChallengeMemberRepository members;
	  private final GoalRepository goals;
	  private final UserRepository users;

	  // 챌린지 생성 (초기에는 아무나 생성 가능, 나중에 ADMIN만 허용해도 됨) 
	  @Transactional
	  public Challenge create(CreateChallengeRequest req) {
	    return challenges.save(Challenge.builder()
	      .title(req.title())
	      .description(req.description())
	      .endsAt(req.endsAt())
	      .status("OPEN")
	      .build());
	  }

	  // 챌린지 참여: 내 goal을 연결 
	  @Transactional
	  public ChallengeMember join(Long userId, Long challengeId, JoinChallengeRequest req) {
	    Challenge ch = challenges.findById(challengeId)
	      .orElseThrow(() -> new IllegalArgumentException("challenge not found"));

	    if ("CLOSED".equalsIgnoreCase(ch.getStatus())) {
	      throw new IllegalArgumentException("challenge closed");
	    }

	    if (members.existsByChallenge_IdAndUser_Id(challengeId, userId)) {
	      throw new IllegalArgumentException("already joined");
	    }

	    // 내 소유 goal만 참가 가능
	    Goal g = goals.findByIdAndUser_Id(req.goalId(), userId)
	      .orElseThrow(() -> new IllegalArgumentException("goal not found or not owned"));

	    User uref = users.getReferenceById(userId);

	    return members.save(ChallengeMember.builder()
	      .challenge(ch)
	      .user(uref)
	      .goal(g)
	      .build());
	  }

	  // 리더보드 계산: goal 달성률로 정렬 
	  @Transactional(readOnly = true)
	  public List<LeaderboardRow> leaderboard(Long challengeId) {
	    List<ChallengeMember> rows = members.leaderboardRows(challengeId);
	    return rows.stream()
	      .map(cm -> {
	        Goal g = cm.getGoal();
	        long current = Optional.ofNullable(g.getCurrentAmount()).orElse(0L);
	        long target  = Optional.ofNullable(g.getTargetAmount()).orElse(0L);
	        int progress = (target == 0) ? 0 : (int)Math.min(100, current * 100 / target);
	        return new LeaderboardRow(
	          cm.getUser().getId(), cm.getUser().getName(),
	          g.getId(), g.getTitle(),
	          current, target, progress
	        );
	      })
	      .sorted(Comparator.<LeaderboardRow>comparingInt(LeaderboardRow::progress).reversed())
	      .toList();
	  }
}
