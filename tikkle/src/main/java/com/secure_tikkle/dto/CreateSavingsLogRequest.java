package com.secure_tikkle.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// 저축 로그(절약 저금) 생성 요청 
public record CreateSavingsLogRequest(
		@NotNull
	    Long goalId,

	    @NotNull @Positive @Max(1_000_000_000L) // 10억 제한 예시
	    Long amount,

	    @Size(max = 200)
	    String memo
	    
) {}
