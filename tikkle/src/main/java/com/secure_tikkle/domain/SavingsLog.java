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
public class SavingsLog {

	  @Id 
	  @GeneratedValue(strategy = GenerationType.IDENTITY) 
	  Long id;
	  
	  @ManyToOne(fetch = FetchType.LAZY) 
	  @JoinColumn(name="goal_id") 
	  private Goal goal;
	  
	  private Long amount;
	  private String memo;
	  
	  private LocalDateTime createdAt;
	  @PrePersist void pp() { 
		  createdAt = LocalDateTime.now(); 
	  }
	  
	  
}
