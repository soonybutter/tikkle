package com.secure_tikkle.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// 목표 생성 요청 바디
// title : 목표 이름( 필수, 최대 60자)
// targetAmoung: 목표 금액(원, 양수)
public record CreateGoalRequest(
	    @NotBlank @Size(max = 60) String title,
	    @NotNull  @Positive @Max(100_000_000_000L) Long   targetAmount
) {}

