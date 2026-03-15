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

	private final GoalRepository goalRepository;
    private final SavingsLogRepository savingsLogRepository;
    private final BadgeService badgeService;

    /** 저축기록 수정(금액/메모), 소유자 검증 포함 */
    @Transactional
    public void updateLog(Long userId, Long logId, long newAmount, String newMemo) {
        SavingsLog log = savingsLogRepository.findByIdAndGoal_User_Id(logId, userId)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "log not found"));

        if (newAmount <= 0) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, "amount must be > 0");
        }

        Goal goal = log.getGoal();
        long delta = newAmount - log.getAmount(); // 증감분

        log.setAmount(newAmount);
        log.setMemo(newMemo == null || newMemo.isBlank() ? null : newMemo.trim());
        savingsLogRepository.save(log);

        // 목표 진행금액 반영
        goal.setCurrentAmount(goal.getCurrentAmount() + delta);
        goalRepository.save(goal);

        // 배지 재평가(키워드/횟수/합계 변화 반영)
        badgeService.evaluateOnSavingsLog(userId);
    }

    /** 저축기록 삭제 */
    @Transactional
    public void deleteLog(Long userId, Long logId) {
        SavingsLog log = savingsLogRepository.findByIdAndGoal_User_Id(logId, userId)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "log not found"));

        Goal goal = log.getGoal();
        goal.setCurrentAmount(goal.getCurrentAmount() - log.getAmount());
        goalRepository.save(goal);

        savingsLogRepository.delete(log);

        // 배지 재평가
        badgeService.evaluateOnSavingsLog(userId);
    }
    
    @Transactional
    public com.secure_tikkle.domain.SavingsLog createLog(Long userId, Long goalId, Long amount, String memo) {
        if (amount == null || amount <= 0) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, "amount must be > 0");
        }

        // 소유자 검증 포함
        var goal = goalRepository.findByIdAndUser_Id(goalId, userId)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "goal not found or not owned"));

        var saved = savingsLogRepository.save(
            com.secure_tikkle.domain.SavingsLog.builder()
                .goal(goal)
                .amount(amount)
                .memo(memo == null || memo.isBlank() ? null : memo.trim())
                .build()
        );

        // 누적액 반영
        long cur = goal.getCurrentAmount() == null ? 0L : goal.getCurrentAmount();
        goal.setCurrentAmount(cur + amount);
        goalRepository.save(goal);

        // 배지 재평가
        badgeService.evaluateOnSavingsLog(userId);

        return saved; // ← 컨트롤러에서 getId(), getAmount() 등 사용 가능
    }
}
