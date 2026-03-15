package com.secure_tikkle.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.secure_tikkle.domain.Goal;

// Goal 조회 시, 반드시 "내 것"만 보이도록
// userId 조건을 항상 함께 사용
public interface GoalRepository extends JpaRepository<Goal, Long> {

	List<Goal> findByUser_IdOrderByIdDesc(Long userId);
    Optional<Goal> findByIdAndUser_Id(Long id, Long userId);

    @Query("select count(g) from Goal g " +
           "where g.user.id = :userId and g.targetAmount is not null and g.currentAmount is not null " +
           "and g.currentAmount >= g.targetAmount")
    
    long countCompletedByUser(@Param("userId") Long userId);
	
}
