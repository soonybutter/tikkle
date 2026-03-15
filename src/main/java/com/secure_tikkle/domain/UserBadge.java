package com.secure_tikkle.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
  name="user_badges",
  uniqueConstraints = @UniqueConstraint(name="uq_user_badge_code", columnNames={"user_id","code"})
)
public class UserBadge {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Long id;

	  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="user_id", nullable=false)
	  private User user;

	  @Column(nullable=false, length=40)
	  private String code;   // FIRST_SAVE / COFFEE_10 / STREAK_7

	  @Column(nullable=false, length=80)
	  private String name;   // 보여줄 이름

	  private String iconUrl;          // 선택
	  private LocalDateTime earnedAt;  // 획득 시각

	  @PrePersist void pp(){ 
		  earnedAt = LocalDateTime.now(); 
	  }
}
