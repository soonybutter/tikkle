package com.secure_tikkle.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.secure_tikkle.domain.Goal;
import com.secure_tikkle.domain.SavingsLog;
import com.secure_tikkle.domain.User;
import com.secure_tikkle.dto.CreateGoalRequest;
import com.secure_tikkle.dto.CreateSavingsLogRequest;
import com.secure_tikkle.dto.GoalDetail;
import com.secure_tikkle.dto.GoalSummaryDto;
import com.secure_tikkle.dto.SavingsLogDto;
import com.secure_tikkle.repository.GoalRepository;
import com.secure_tikkle.repository.SavingsLogRepository;
import com.secure_tikkle.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final SavingsLogRepository savingsLogRepository;
    private final UserRepository userRepository;
    private final BadgeService badgeService;

    // 목표 생성
    @Transactional
    public GoalSummaryDto create(Long userId, CreateGoalRequest req) {  
        User userRef = userRepository.getReferenceById(userId);
        Goal goal = Goal.builder()
                .user(userRef)
                .title(req.title())
                .targetAmount(req.targetAmount())
                .currentAmount(0L)
                .build();
        Goal saved = goalRepository.save(goal);
        return toSummary(saved); //  엔티티 -> DTO
    }
    
    // 내 목표 목록 (요약)
    @Transactional(readOnly = true)
    public List<GoalSummaryDto> list(Long userId) {
        return goalRepository.findByUser_IdOrderByIdDesc(userId)
                .stream()
                .map(this::toSummary)
                .toList();
    }

    // 이름을 myGoals로 쓰고 싶다면 list를 재사용
    @Transactional(readOnly = true)
    public List<GoalSummaryDto> myGoals(Long userId) {
        return list(userId);
    }

    // 단건 조회 (요약)
    @Transactional(readOnly = true)
    public GoalSummaryDto one(Long userId, Long goalId) {
        var g = goalRepository.findByIdAndUser_Id(goalId, userId).orElseThrow();
        return toSummary(g);
    }

    // 상세 조회
    @Transactional(readOnly = true)
    public GoalDetail detail(Long userId, Long goalId) {
        Goal g = goalRepository.findByIdAndUser_Id(goalId, userId)
                .orElseThrow(() -> new IllegalArgumentException("goal not found"));
        long current = g.getCurrentAmount() == null ? 0L : g.getCurrentAmount();
        long target  = g.getTargetAmount() == null ? 0L : g.getTargetAmount();
        int progress = (target == 0) ? 0 : (int) Math.min(100, (current * 100 / target));
        return new GoalDetail(g.getId(), g.getTitle(), g.getTargetAmount(), current, progress);
    }

    // 절약 저금 기록
    @Transactional
    public SavingsLog addLog(Long userId, CreateSavingsLogRequest req) {
        Goal goal = goalRepository.findByIdAndUser_Id(req.goalId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("goal not found or not owned"));

        SavingsLog saved = savingsLogRepository.save(SavingsLog.builder()
                .goal(goal)
                .amount(req.amount())
                .memo(req.memo())
                .build());

        long current = goal.getCurrentAmount() == null ? 0L : goal.getCurrentAmount();
        goal.setCurrentAmount(current + req.amount());

        // 배지 평가 훅
        badgeService.evaluateOnSavingsLog(userId);
        return saved;
    }

    // 로그 페이지 조회 (DTO)
    @Transactional(readOnly = true)
    public Page<SavingsLogDto> logs(Long userId, Long goalId, int page, int size) {
        goalRepository.findByIdAndUser_Id(goalId, userId)
                .orElseThrow(() -> new IllegalArgumentException("goal not found or not owned"));

        return savingsLogRepository
                .findByGoal_Id(goalId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")))
                .map(l -> new SavingsLogDto(
                        l.getId(),
                        goalId,
                        l.getAmount(),
                        l.getMemo(),
                        l.getCreatedAt()
                ));
    }

    // 로그 삭제 (누적액 보정)
    @Transactional
    public void deleteLog(Long userId, Long logId) {
        var log = savingsLogRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("log not found"));
        var goal = log.getGoal();
        if (!goal.getUser().getId().equals(userId)) throw new IllegalArgumentException("forbidden");

        long cur = goal.getCurrentAmount() == null ? 0 : goal.getCurrentAmount();
        long amt = log.getAmount() == null ? 0 : log.getAmount();
        goal.setCurrentAmount(cur - amt);
        savingsLogRepository.delete(log);
    }

    // 로그 수정 (누적액 보정)
    @Transactional
    public SavingsLog updateLog(Long userId, Long logId, Long newAmount, String newMemo) {
        var log = savingsLogRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("log not found"));
        var goal = log.getGoal();
        if (!goal.getUser().getId().equals(userId)) throw new IllegalArgumentException("forbidden");

        long before = log.getAmount() == null ? 0 : log.getAmount();
        long after  = newAmount == null ? before : newAmount;

        log.setAmount(after);
        if (newMemo != null) log.setMemo(newMemo);

        long cur = goal.getCurrentAmount() == null ? 0 : goal.getCurrentAmount();
        goal.setCurrentAmount(cur - before + after);
        return log;
    }

    
    private GoalSummaryDto toSummary(Goal g) {
        long cur = g.getCurrentAmount() == null ? 0L : g.getCurrentAmount();
        long tgt = g.getTargetAmount() == null ? 0L : g.getTargetAmount();
        int prog = (tgt == 0) ? 0 : (int) Math.min(100, (cur * 100) / tgt);
        return new GoalSummaryDto(g.getId(), g.getTitle(), tgt, cur, prog);
    }
}
