package com.secure_tikkle.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@Table(name="badge", indexes = { @Index(name="ix_badge_code", columnList="code", unique=true) })
public class Badge {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false, length=50) 
	private String code; // ex. COFFEE_10
	
	@Column(nullable=false, length=100) 
	private String title; // 커피 10번 참기
	
	@Column(nullable=false, length=255) 
	private String description; // 커피를 10번 절약했어요!
	
	@Column(nullable=false, length=50) 
	private String icon; // 이모지 or 아이콘 텍스트
	
	@Enumerated(EnumType.STRING)
    @Column(name = "condition_type", nullable = false, length = 30)
    private BadgeConditionType conditionType;
	
	@Column(nullable=false) 
	private Long threshold;
	
	@Column(length=50) 
	private String keyword;
	
	public enum ConditionType { SAVINGS_COUNT, SAVINGS_SUM, MEMO_COUNT, GOAL_COMPLETED }
	
}
