package com.secure_tikkle.dto;

// 목표 목록 요약용 DTO 
public record GoalSummaryDto(
		Long id,
	    String title,
	    Long targetAmount,
	    Long currentAmount,
	    int progress
	    
) {}
