package com.secure_tikkle.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity 
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
@Table(indexes = @Index(columnList = "code", unique = true))
public class RankInvite {

	  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Long id;

	  @Column(nullable = false)
	  private Long groupId;

	  @Column(nullable = false, length = 16)
	  private String code; // 초대 코드(짧은 문자열)

	  private Integer maxUses; // null이면 무제한
	  private Integer usedCount; // 기본 0

	  private Instant expiresAt; // null이면 만료 없음
	  private Long createdBy;
	  private Instant createdAt;
}
