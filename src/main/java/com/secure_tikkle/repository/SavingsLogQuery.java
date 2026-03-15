package com.secure_tikkle.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.secure_tikkle.domain.SavingsLog;

public interface SavingsLogQuery extends JpaRepository<SavingsLog, Long> {

	  @Query("select count(s) from SavingsLog s where s.goal.user.id = :userId")
	  long countAllByUser(@Param("userId") Long userId);

	  @Query("select count(s) from SavingsLog s where s.goal.user.id = :userId and lower(s.memo) like lower(concat('%',:kw,'%'))")
	  long countByUserAndMemoContains(@Param("userId") Long userId, @Param("kw") String keyword);

	  @Query("""
	     select s from SavingsLog s 
	       where s.goal.user.id = :userId 
	         and s.id in (
	           select s2.id from SavingsLog s2 
	           where s2.goal.user.id = :userId and s2.amount > 0 
	             and s2.createdAt between :from and :to
	         )
	  """)
	  List<SavingsLog> logsIn(@Param("userId") Long userId,
	                          @Param("from") LocalDateTime from,
	                          @Param("to") LocalDateTime to);
}
