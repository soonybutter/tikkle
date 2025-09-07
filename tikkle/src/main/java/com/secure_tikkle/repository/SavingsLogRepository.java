package com.secure_tikkle.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
	
	    @Query("select coalesce(sum(s.amount),0) from SavingsLog s " +
	           "where s.goal.user.id = :userId and s.amount > 0")
	    Long sumAmountByUser(@Param("userId") Long userId);
	
	    // 여러 키워드를 정규식(OR)으로 매칭해서 메모 카운트
	    // 여러 문자열있어도 배지 카운트에 인식 기능하도록
	    @Query(value = """
	        select count(distinct s.id)
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