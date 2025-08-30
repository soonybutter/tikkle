package com.secure_tikkle.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class ChallengeDtos {

	 private ChallengeDtos(){}

	  public record CreateChallengeRequest(
	      @NotBlank @Size(max=80) String title,
	      @Size(max=200) String description,
	      LocalDate endsAt
	  ) {}

	  public record JoinChallengeRequest(@NotNull Long goalId) {}

	  public record LeaderboardRow(
	      Long userId, String userName,
	      Long goalId, String goalTitle,
	      long current, long target,
	      int progress
	  ) {}
	  
}
