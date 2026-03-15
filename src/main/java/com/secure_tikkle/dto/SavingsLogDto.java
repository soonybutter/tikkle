package com.secure_tikkle.dto;

import java.time.LocalDateTime;

public record SavingsLogDto(

		Long id, 
		Long goalId, 
		Long amount, 
		String memo, 
		LocalDateTime createdAt
	    
) {}
