package com.secure_tikkle.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import com.secure_tikkle.domain.UserBadge;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

	boolean existsByUser_IdAndBadge_Id(Long userId, Long badgeId);
    List<UserBadge> findByUser_Id(Long userId);
	
}
