package com.secure_tikkle.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.secure_tikkle.domain.Badge;
import com.secure_tikkle.domain.BadgeConditionType;
import com.secure_tikkle.domain.User;
import com.secure_tikkle.domain.UserBadge;
import com.secure_tikkle.repository.BadgeRepository;
import com.secure_tikkle.repository.GoalRepository;
import com.secure_tikkle.repository.SavingsLogRepository;
import com.secure_tikkle.repository.UserBadgeRepository;
import com.secure_tikkle.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BadgeService {

	private final BadgeRepository badgeRepository;        // 모든 배지 + 여부
	private final UserBadgeRepository userBadgeRepository; // 획득한 배지만
    private final SavingsLogRepository savingsLogRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    
    
    // 모든 배지 + 내가 땄는지 여부 표시
    public java.util.List<com.secure_tikkle.dto.BadgeDto> listForUser(Long userId) {
      return badgeRepository.findAllWithEarned(userId);
    }

    // 저축 로그 발생 시 배지 평가
    @Transactional
    public List<Badge> evaluateOnSavingsLog(Long userId) {
    	
        List<Badge> unlockedNow = new ArrayList<>();

        User userRef = userRepository.getReferenceById(userId);

        // 집계 값 미리 계산
        long savingsCount = savingsLogRepository.countByGoal_User_IdAndAmountGreaterThan(userId, 0L);
        long completed    = goalRepository.countCompletedByUser(userId);
        long sumAmount    = savingsLogRepository.sumAmountByUser(userId);

        for (Badge b : badgeRepository.findAll()) {
            boolean meet = false;
            Long th = b.getThreshold() == null ? 0L : b.getThreshold();

            BadgeConditionType t = b.getConditionType();
            switch (t) {
	            case SAVINGS_COUNT -> meet = savingsCount >= th;
	            case SAVINGS_SUM   -> meet = sumAmount   >= th;
	
	            case MEMO_COUNT -> {
	                
	                String kwRaw = b.getKeyword();
	
	                // 콤마/파이프 구분자 모두 허용 -> 소문자 변환 -> 정규식 특수문자 이스케이프 -> OR로 묶기
	                String pattern = java.util.Arrays.stream(
	                            kwRaw == null ? new String[0] : kwRaw.split(",|\\|")
	                        )
	                        .map(String::trim)
	                        .filter(s -> !s.isEmpty())
	                        .map(String::toLowerCase)
	                        .map(s -> s.replaceAll("[.*+?^${}()\\[\\]\\\\|]", "\\\\$0"))
	                        .collect(java.util.stream.Collectors.joining("|")); // "a|b|c"
	
	                long cnt = pattern.isBlank()
	                        ? 0L
	                        : savingsLogRepository.countByUserMemoRegex(userId, pattern);
	
	                meet = cnt >= th;
	            }
	
	            case GOAL_COMPLETED -> meet = completed >= th;
            }

            if (meet && !userBadgeRepository.existsByUser_IdAndBadge_Id(userId, b.getId())) {
            	var ub = UserBadge.builder()
            	        .user(userRef)
            	        .badge(b)
            	        .build();
            	ub.setUnlockedAt(LocalDateTime.now());
            	userBadgeRepository.save(ub);
                unlockedNow.add(b);
            }
        }
        return unlockedNow; 
    }
    
    
    
}
