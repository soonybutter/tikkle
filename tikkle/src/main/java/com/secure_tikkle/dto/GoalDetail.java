package com.secure_tikkle.dto;

// 목표 상세 DTO (진행률 포함)
public record GoalDetail(
		Long id,
	    String title,
	    Long targetAmount,
	    Long currentAmount,
	    int  progress
) {}
