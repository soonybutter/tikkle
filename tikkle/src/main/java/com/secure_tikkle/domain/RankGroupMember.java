package com.secure_tikkle.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"groupId","userId"}))
public class RankGroupMember {

	  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Long id;

	  @Column(nullable = false)
	  private Long groupId;

	  @Column(nullable = false)
	  private Long userId;

	  @Column(nullable = false)
	  private String role; // "OWNER" or "MEMBER"

	  @Column(nullable = false)
	  private Instant joinedAt;
}
