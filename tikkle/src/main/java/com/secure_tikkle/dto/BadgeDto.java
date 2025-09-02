package com.secure_tikkle.dto;

import java.time.LocalDateTime;

public record BadgeDto(

		String code,
		String title,
		String description,
		String icon,
		boolean earned,
		LocalDateTime earnedAt
) {}
