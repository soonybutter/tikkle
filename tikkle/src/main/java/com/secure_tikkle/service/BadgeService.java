package com.secure_tikkle.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.secure_tikkle.domain.User;
import com.secure_tikkle.domain.UserBadge;
import com.secure_tikkle.repository.SavingsLogQuery;
import com.secure_tikkle.repository.UserBadgeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class BadgeService {

	private final UserBadgeRepository userBadges;
	  private final SavingsLogQuery logQuery;

	  // SavingsLog 생성 이후 호출: 조건 만족 시 배지 발급 
	  @Transactional
	  public List<UserBadge> evaluateOnSavingsLog(Long userId) {
	    
		  List<UserBadge> newly = new ArrayList<>();

	    // 1) FIRST_SAVE
	    if (!userBadges.existsByUser_IdAndCode(userId, "FIRST_SAVE")) {
	      long total = logQuery.countAllByUser(userId);
	      if (total >= 1) newly.add(grant(userId, "FIRST_SAVE", "첫 저축 성공", null));
	    }

	    // 2) COFFEE_10 (메모에 '커피' 10회)
	    if (!userBadges.existsByUser_IdAndCode(userId, "COFFEE_10")) {
	      long coffee = logQuery.countByUserAndMemoContains(userId, "커피");
	      if (coffee >= 10) newly.add(grant(userId, "COFFEE_10", "커피 10번 참기", null));
	    }

	    // 3) STREAK_7 (7일 연속)
	    if (!userBadges.existsByUser_IdAndCode(userId, "STREAK_7")) {
	      if (hasStreak(userId, 7)) newly.add(grant(userId, "STREAK_7", "7일 연속 저축", null));
	    }

	    return newly;
	  }

	  private UserBadge grant(Long userId, String code, String name, String iconUrl) {
	    
		  User u = new User(); u.setId(userId); // 프록시 대용
		  
	    return userBadges.save(UserBadge.builder()
	      .user(u).code(code).name(name).iconUrl(iconUrl).build());
	  }

	  // 단순 연속성 체크: 오늘 포함 역방향으로 하루 1개 이상 로그가 7일 연속 존재하는지 
	  private boolean hasStreak(Long userId, int days) {
		  
	    LocalDate today = LocalDate.now();
	    
	    for (int i=0; i<days; i++) {
	      LocalDate d = today.minusDays(i);
	      LocalDateTime from = d.atStartOfDay();
	      LocalDateTime to   = d.plusDays(1).atStartOfDay().minusNanos(1);
	      
	      var logs = logQuery.logsIn(userId, from, to);
	      if (logs.isEmpty()) return false;
	      
	    }
	    
	    return true;
	    
	  }
}
