package com.secure_tikkle.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.secure_tikkle.domain.ChallengeMember;

public interface ChallengeMemberRepository extends JpaRepository<ChallengeMember, Long> {

	boolean existsByChallenge_IdAndUser_Id(Long challengeId, Long userId);
	  List<ChallengeMember> findByChallenge_Id(Long challengeId);

	  @Query("""
	    select cm from ChallengeMember cm
	      join fetch cm.user u
	      join fetch cm.goal g
	    where cm.challenge.id = :cid
	  """)
	  
	  List<ChallengeMember> leaderboardRows(@Param("cid") Long challengeId);
	  
}
