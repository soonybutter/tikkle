package com.secure_tikkle.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
@Table(
  name = "challenge_members",
  uniqueConstraints = @UniqueConstraint(name="uq_challenge_user", columnNames = {"challenge_id","user_id"})
)
public class ChallengeMember {

	// challengeMember: goal을 연결해두면 랭킹 계산 쉬워짐.
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Long id;

	  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="challenge_id", nullable=false)
	  private Challenge challenge;

	  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="user_id", nullable=false)
	  private User user;

	  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="goal_id", nullable=false)
	  private Goal goal;   // 내 챌린지에 연결할 목표 (달성률 계산의 기준)

	  private LocalDateTime joinedAt;

	  @PrePersist void pp() { 
		  joinedAt = LocalDateTime.now(); 
	  }
}
