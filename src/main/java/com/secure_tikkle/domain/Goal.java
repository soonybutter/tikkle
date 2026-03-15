package com.secure_tikkle.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
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
public class Goal {

	  @Id 
	  @GeneratedValue(strategy = GenerationType.IDENTITY) 
	  Long id;
	  
	  @ManyToOne(fetch = FetchType.LAZY) 
	  @JoinColumn(name="user_id") 
	  private User user;
	  
	  private String title;
	  private Long targetAmount;
	  private Long currentAmount;
	  
	  @Version
	  private Long version;
	  
}
