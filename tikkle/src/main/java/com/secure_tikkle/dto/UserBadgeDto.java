package com.secure_tikkle.dto;

import java.time.LocalDateTime;

public record UserBadgeDto(
		  String code,
		  String name,
		  String iconUrl,
		  LocalDateTime earnedAt
) {}
