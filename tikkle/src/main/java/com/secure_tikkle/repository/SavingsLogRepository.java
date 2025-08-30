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
	
	  // goal.id 기준으로 조회
	  List<SavingsLog> findByGoal_IdOrderByIdDesc(Long goalId);

	  // 페이징
	  Page<SavingsLog> findByGoal_Id(Long goalId, Pageable pageable);

	  // 합계
	  @Query("select coalesce(sum(s.amount),0) from SavingsLog s where s.goal.id = :goalId")
	  Long sumByGoalId(@Param("goalId") Long goalId);
	  
}