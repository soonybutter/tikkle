package com.secure_tikkle.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateSavingsRequest(

		@NotNull @Min(1) 
		Long amount,
	    String memo
		
) {}
