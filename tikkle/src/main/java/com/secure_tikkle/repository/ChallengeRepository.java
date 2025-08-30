package com.secure_tikkle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.secure_tikkle.domain.Challenge;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

}
