package com.secure_tikkle.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.secure_tikkle.domain.SavingsLog;


// 단순 CRUD면 JpaRepository만으로 충분.
// 합계/통계가 필요하면 @Query 추가로 확장한다.
public interface SavingsLogRepository extends JpaRepository<SavingsLog, Long> {
    
	List<SavingsLog> findByGoal_IdOrderByIdDesc(Long goalId);

    Page<SavingsLog> findByGoal_Id(Long goalId, Pageable pageable);

    long countByGoal_User_IdAndAmountGreaterThan(Long userId, Long amount);

    long countByGoal_User_IdAndMemoContaining(Long userId, String keyword);
    
    // 로그 수정/삭제 시 "내 로그인지" 소유자 검증용
    Optional<SavingsLog> findByIdAndGoal_User_Id(Long logId, Long userId);
    
    List<SavingsLog> findByGoal_IdAndGoal_User_IdOrderByIdDesc(Long goalId, Long userId);

    @Modifying
    void deleteByGoal_Id(Long goalId);
    
    // 누적 금액 합계 (없다면 추가, 이미 있으면 중복 정의 제거)
    @Query("""
           select coalesce(sum(s.amount), 0)
           from SavingsLog s
           where s.goal.user.id = :userId and s.amount > 0
           """)
    Long sumAmountByUser(@Param("userId") Long userId);

    // 메모 다중 키워드 정규식 매칭 (MySQL REGEXP)
    @Query(value = """
        select count(*) 
        from savings_log s
        join goal g on s.goal_id = g.id
        where g.user_id = :userId
          and s.amount > 0
          and s.memo is not null
          and lower(s.memo) regexp :pattern
        """, nativeQuery = true)
    long countByUserMemoRegex(@Param("userId") Long userId,
                              @Param("pattern") String pattern);
    
}