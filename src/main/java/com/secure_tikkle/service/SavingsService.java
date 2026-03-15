package com.secure_tikkle.service;

import org.springframework.stereotype.Service;

import com.secure_tikkle.domain.Goal;
import com.secure_tikkle.domain.SavingsLog;
import com.secure_tikkle.repository.GoalRepository;
import com.secure_tikkle.repository.SavingsLogRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavingsService {

	  private final GoalRepository goals;
	  private final SavingsLogRepository logs;

	  @Transactional
	  public SavingsLog save(Long userId, Long goalId, Long amount, String memo) {
	    //  소유권 검증(내 목표만 적립 가능)
	    Goal g = goals.findByIdAndUser_Id(goalId, userId).orElseThrow();

	    // null 안전
	    if (g.getCurrentAmount() == null) g.setCurrentAmount(0L);
	    g.setCurrentAmount(g.getCurrentAmount() + amount);

	    // 적립 로그 저장
	    SavingsLog log = logs.save(SavingsLog.builder()
	        .goal(g).amount(amount).memo(memo)
	        .build());

	    // Goal 의 @Version 으로 낙관적 락 적용됨(동시 업데이트 시 실패)
	    // 필요하면 try-catch로 사용자 친화 에러 변환
	    return log;
	  }
}
