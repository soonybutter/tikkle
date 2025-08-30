package com.secure_tikkle.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateSavingsLogRequest(

	@Positive @Max(1_000_000_000L)
	Long amount,              // null이면 금액 변경 없음
	
	@Size(max = 200)
	String memo   
		
) {
	
	
}
