package com.secure_tikkle.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.secure_tikkle.domain.UserBadge;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

	boolean existsByUser_IdAndCode(Long userId, String code);
	long countByUser_IdAndCode(Long userId, String code);
	
	// 내가 가진 배지 최신순
	List<UserBadge> findByUser_IdOrderByEarnedAtDesc(Long userId);
	
}
