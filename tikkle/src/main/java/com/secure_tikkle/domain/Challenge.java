package com.secure_tikkle.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "challenges")
public class Challenge {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Long id;

	  @Column(nullable=false, length=80)
	  private String title;              // 챌린지 이름 (예: "9월 커피 참기")

	  @Column(length=200)
	  private String description;        // 설명

	  private LocalDate endsAt;          // 종료일(선택)

	  @Column(nullable=false, length=16)
	  private String status;             // OPEN / CLOSED

	  private LocalDateTime createdAt;

	  @PrePersist void pp() {
	    createdAt = LocalDateTime.now();
	    if (status == null) status = "OPEN";
	  }
}
